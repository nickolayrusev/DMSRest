package org.mongo.services.impl;

import static org.mongo.utils.CommonUtils.parseCampaignByPage;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import java.util.List;

import org.mongo.domain.Campaign;
import org.mongo.services.CampaignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Service;

@Service("campaignService")
public class CampaignServiceImpl implements CampaignService {
	private static final Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);
	
	@Autowired
	MongoOperations mongoTemplate;
	
	@Override
	public List<Campaign> getCampaigns(Integer type, Integer page) {
		logger.info("return campaigns");
		List<Campaign> parseCampaignByPage = parseCampaignByPage(page, type);
		return parseCampaignByPage;

	}
	
	@Override
	public List<Campaign> getCampaigns(List<Long> campaignIds){
		List<Campaign> campaigns = mongoTemplate.find(query(where("campaignId").in(campaignIds)), Campaign.class);
		return campaigns;
	}
}
