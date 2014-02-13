package org.mongo.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.mongo.config.RootConfig;
import org.mongo.config.WebConfig;
import org.mongo.controller.HomeController;
import org.mongo.services.CampaignService;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration( classes={WebConfig.class,RootConfig.class})
public class MockitoTest {
	
	@InjectMocks
	private HomeController controller;
	
	@Mock
	private CampaignService campaignService;

	
	@Before
    public void setup() throws IOException {
		MockitoAnnotations.initMocks(this);
		List<Campaign> lstCampaign = new ArrayList<Campaign>();
		Campaign camp1 = new Campaign();
		camp1.setDescription("leeds united");

		Campaign camp2 = new Campaign();
		camp2.setDescription("manchester united southampton");

		Campaign camp3 = new Campaign();
		camp3.setDescription("stoke sunderland");
		
		lstCampaign.add(camp1);
		lstCampaign.add(camp2);
		lstCampaign.add(camp3);
		
		when(controller.campaigns(1, 1)).thenReturn(new ArrayList<Campaign>());
		when(controller.campaigns(1, 0)).thenReturn(lstCampaign);
	}
	@Test
	public void testPeopleCampaign() throws IOException{
		
		List<Campaign> campaigns = controller.campaigns(1, 1);
		assertEquals(0, campaigns.size());
		
		List<Campaign> campaignsSecond = controller.campaigns(1, 0);
		assertEquals(0, campaignsSecond.size());
	}
	
	@Test
	public void testApacheLucene() throws CorruptIndexException, LockObtainFailedException, IOException, ParseException{
		//List<String> l = Arrays.asList("this is a test page","manchester","newcastle","leeds","stevenegae");
 
		List<Campaign> l = controller.campaigns(1, 0);
        // create some index
        // we could also create an index in our ram ...
        // Directory index = new RAMDirectory();
        Directory index = new RAMDirectory();
        StandardAnalyzer analyzer = new StandardAnalyzer();
        IndexWriter w = new IndexWriter(index, analyzer, true,
                IndexWriter.MaxFieldLength.UNLIMITED);
 
        // index some data
        for (Campaign i : l) {
            System.out.println("indexing " + i);
            Document doc = new Document();
            doc.add(new Field("title", i.getDescription(), Field.Store.YES,
                            Field.Index.ANALYZED));
            w.addDocument(doc);
        }
 
        w.close();
        System.out.println("index generated");
        // parse query over multiple fields
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(new String[]{"title"},
                analyzer);
        multiFieldQueryParser.setAllowLeadingWildcard(true);
        Query q = multiFieldQueryParser.parse("*ted*");
 
        // searching ...
        int hitsPerPage = 10;
        IndexSearcher searcher = new IndexSearcher(index);
        TopDocCollector collector = new TopDocCollector(hitsPerPage);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
 
        // output results
        System.out.println("Found " + hits.length + " hits.");
        for (int i = 0; i < hits.length; ++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " +  ": "
                    + d.get("title"));
        }
	}
    
}
