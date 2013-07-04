package org.mongo.domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
		List<Campaign> lstCampaing = new ArrayList<Campaign>();
		lstCampaing.add(new Campaign());
		lstCampaing.add(new Campaign());
		
		when(controller.campaigns(1, 1)).thenReturn(new ArrayList<Campaign>());
		when(controller.campaigns(1, 0)).thenReturn(lstCampaing);
	}
	@Test
	public void testPeopleCampaign() throws IOException{
		
		List<Campaign> campaigns = controller.campaigns(1, 1);
		assertEquals(0, campaigns.size());
		
		List<Campaign> campaignsSecond = controller.campaigns(1, 0);
		assertEquals(0, campaignsSecond.size());
	}
	
    
}
