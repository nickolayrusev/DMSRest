package org.mongo.index;

import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.Version;
import org.mongo.domain.Campaign;
import org.mongo.repository.CampaignRepository;
import org.mongo.services.CampaignService;
import org.mongo.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class IndexComponent {
	
	private static final Logger logger = LoggerFactory.getLogger(IndexComponent.class);
	
	@Autowired
	private IndexWriter writer;
	
	@Autowired
	private IndexSearcher searcher;
	
	@Autowired
	private Version luceneVersion;

	@Autowired
	private Analyzer  analyzer;
	
	@Autowired
	private IndexReader reader;
	
	@Autowired
	CampaignRepository campaignRepository;
	
	@Autowired
	CampaignService campaignService;
	
	public List<Campaign> searchCampaigns(String term) throws ParseException, IOException{
		List<Campaign> camapigns = new ArrayList<Campaign>();
		Query q = new MultiFieldQueryParser(luceneVersion, new String[]{"description","title"}, analyzer).parse(term);
		int hitsPerPage = 100;
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		logger.info("hits are: " + hits.length);
		for(ScoreDoc scoreDoc : hits){
			int docId = scoreDoc.doc;
			Document doc = searcher.doc(docId);
			Long campaignId = doc.getField("campaignId").numericValue().longValue();
			logger.info("searching by campaign id:" + campaignId);
			Campaign findByCampaignId = campaignRepository.findByCampaignId(campaignId);
			camapigns.add(findByCampaignId);
		}
		
		return camapigns;
	}
	
	public List<Campaign> searchCampaigns(String term,List<Integer> type) throws IOException{
		notEmpty(type);
		Collections.sort(type);
		Query query5;
		try {
			query5 = new MultiFieldQueryParser(luceneVersion, new String[]{"description","title","longDescription"}, analyzer).parse(term);
		} catch (ParseException e) {
			logger.error("parse exception",e);
			return Collections.emptyList();
		}
		
		Query query3 = NumericRangeQuery.newIntRange("type", type.get(0), type.get(type.size()-1), true, true);
		BooleanQuery booleanQuery = new BooleanQuery();
		booleanQuery.add(query5,BooleanClause.Occur.MUST);
		booleanQuery.add(query3,BooleanClause.Occur.MUST);
		
		logger.info("lucene query is: " + booleanQuery.toString());
		int hitsPerPage = 100;
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		try {
			searcher.search(booleanQuery, collector);
		} catch (IOException e) {
			logger.error("io exception",e);
			return Collections.emptyList();
		}
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		logger.info("hits are: " + hits.length);
		List<Long> hitsList = new ArrayList<Long>(hits.length);
		
		for(ScoreDoc scoreDoc : hits){
			int docId = scoreDoc.doc;
			Document doc = searcher.doc(docId);
			Long campaignId = doc.getField("campaignId").numericValue().longValue();
			hitsList.add(campaignId);
		}
		List<Campaign> campaigns = campaignService.getCampaigns(hitsList);
		return campaigns;
	}
	public Campaign indexCampaign(Campaign campaign) throws CorruptIndexException, IOException{
		notNull(campaign, "campaign is required to be indexed");
		//http://oak.cs.ucla.edu/cs144/projects/lucene/index.html
		Document doc = new Document();
		doc.add(new TextField("description", CommonUtils.normalizeSpaces( campaign.getDescription() ),Store.NO));
		doc.add(new LongField("campaignId",campaign.getCampaignId(), Store.YES));
		doc.add(new TextField("title", campaign.getTitle(), Store.NO));
        doc.add(new IntField("type", campaign.getType(),Field.Store.NO));
        writer.addDocument(doc);
		return campaign;
	}
	public List<Long> getAllIndexedCampaignIds() throws IOException{
		List<Long> ids = new ArrayList<Long>();
		for (int i=0; i<reader.maxDoc(); i++) {
		    Document doc = reader.document(i);
		    Long docId = doc.getField("campaignId").numericValue().longValue();
		    ids.add(docId);
		}
		return ids;
	}
	public void closeWriter() throws CorruptIndexException, IOException{
		if (writer != null) {
            writer.close();
        }
	}
	
	public void deleteAllIndexes() throws IOException{
		if (writer != null) {
            writer.deleteAll();
        }
	}
	public void commit() throws CorruptIndexException, IOException{
		if(writer != null){
			writer.commit();
		}
	}
	public void retrieveCampaigns() throws IOException{
		List<Campaign> campaigns = CommonUtils.parseAllCampaigns();
//		deleteAllIndexes();
//		commit();
		// index some data
        for (Campaign campaign : campaigns) {
            logger.info("indexing : " + campaign.getCampaignId());
            indexCampaign(campaign);
        }
        closeWriter();
	}

}
