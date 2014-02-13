package org.mongo.services;

import java.util.List;

import org.mongo.domain.Campaign;

public interface CampaignService {
	List<Campaign> getCampaigns(Integer type,Integer page);

	List<Campaign> getCampaigns(List<Long> campaignIds);
}
