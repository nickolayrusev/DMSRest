package org.mongo.domain;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongo.config.RootConfig;
import org.mongo.config.WebConfig;
import org.mongo.repository.CampaignRepository;
import org.mongo.services.CampaignService;
import org.mongo.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@ContextConfiguration(classes={WebConfig.class,RootConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class ServiceTest {
	@Autowired
	MongoTemplate template;
	
	@Autowired
	CampaignService campaignService;
	
	@Autowired
	CampaignRepository repository;
	
	@Autowired
	IndexWriter writer;
	
	@Autowired
	IndexSearcher searcher;
	
	@Autowired
	Analyzer analyzer;
	
	private static Logger logger = LoggerFactory.getLogger(ServiceTest.class);
	
	@Test
	public void testGetCollectionsNames() throws Exception {
		logger.info(template.getCollectionNames().toString());
	}
	@Test
	public void parseCampaignLongDescription(){
		CommonUtils.parseCampaignLongDescription("http://dmsbg.com/index.php?page=4&spage=1&item=386&p=1");
	}
	@Test
	public void testNormalizeSpace(){
		String normalizeSpaces = CommonUtils.normalizeSpaces("Hello world from.nickolay rusev");
		logger.info(normalizeSpaces);
	}
	@Test
	public void testSaveCampaign(){
		Campaign campaign = new Campaign();
		campaign.setText("a");
		campaign.setDescription("desc");
		repository.save(campaign);
	}
	@Test
	public void testList(){
		List<String> lst = Arrays.asList("a","b","c");
		logger.info("list is: " +lst);
		
	}
	@Test
	public void testParseCampaigns() throws CorruptIndexException, IOException{
		List<Campaign> campaigns = CommonUtils.parseAllCampaigns();
		
		for (Campaign campaign : campaigns) {
			if(repository.findByCampaignId(campaign.getCampaignId())==null){
				repository.save(campaign);
			}
		}
	}
	@Test
	public void testDeleteAll(){
		List<Campaign> findAll = repository.findAll();
		for (Campaign campaign : findAll) {
			repository.delete(campaign);
		}
	}
	
	@Test
	public void testGetCampaignsInClause(){
		List<Campaign> campaigns = campaignService.getCampaigns(Arrays.asList(218L,411L));
		logger.info(campaigns.toString());
	}
	
	@Test
	public void testGetCampaignById(){
		Campaign findByCampaignId = repository.findByCampaignId(3141L);
		logger.info(findByCampaignId+"");
	}
	@Test
	public void readLucene() throws ParseException, IOException {
		//http://www.lucenetutorial.com/lucene-in-5-minutes.html
		Query q = new QueryParser(Version.LUCENE_31, "description", analyzer).parse("Дария");
		int hitsPerPage = 10;
		TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		int docId = hits[0].doc;
		Document doc = searcher.doc(docId);
		logger.info("result is:"+doc.get("description"));
    }
}
