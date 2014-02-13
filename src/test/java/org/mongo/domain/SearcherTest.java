package org.mongo.domain;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mongo.config.RootConfig;
import org.mongo.config.WebConfig;
import org.mongo.index.IndexComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@WebAppConfiguration
@ContextConfiguration(classes={WebConfig.class,RootConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SearcherTest {
	@Autowired
	IndexComponent component;
	
	@Autowired
	IndexReader reader;
	
	@Test
	public void testSearch() throws ParseException, IOException{
		List<Campaign> searchCampaigns = component.searchCampaigns("парализа");
		assertNotNull(searchCampaigns);
		
	}
	
	@Test
	public void testSearchCampaigns() throws ParseException, IOException{
		List<Campaign> searchCampaigns = component.searchCampaigns("парализа",Arrays.asList(1,2));
		assertNotNull(searchCampaigns);
		
	}
	
	@Test
	public void testReadAll() throws IOException{
		for (int i=0; i<reader.maxDoc(); i++) {
		    Document doc = reader.document(i);
		    String docId = doc.get("campaignId");
//		    System.out.println(docId);
		    System.out.println(doc.getFields());
		    // do something with docId here...
		}
	}
}
