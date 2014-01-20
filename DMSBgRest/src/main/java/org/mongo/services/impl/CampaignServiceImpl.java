package org.mongo.services.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.MemcachedClient;
import org.mongo.domain.Campaign;
import org.mongo.services.CampaignService;
import org.mongo.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("campaignService")
public class CampaignServiceImpl implements CampaignService {
	private static final Logger logger = LoggerFactory.getLogger(CampaignServiceImpl.class);
	
	@Autowired
	MemcachedClient memCachier;
	/**
	 * Getting campaigns by page and type and store the list of campaigns into 
	 * memcachier cache .
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Campaign> getCampaigns(Integer type, Integer page) {
		//cache key is in format two digits (type and page) . ex: 01,12
		String cacheKey = String.valueOf(page)+String.valueOf(type);
		if(memCachier.get(cacheKey)==null){
			logger.info("storing in cache");
			List<Campaign> parseCampaignByPage = CommonUtils.parseCampaignByPage(page, type);
			memCachier.set(cacheKey,(int)TimeUnit.HOURS.toSeconds(8), parseCampaignByPage);
			return parseCampaignByPage;
		}else{
			logger.info("getting from cache "+ cacheKey);
			return (List<Campaign>) memCachier.get(cacheKey);
		}
		
		
		
	}


}
