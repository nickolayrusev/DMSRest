package org.mongo.repository;

import org.mongo.domain.Campaign;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CampaignRepository extends MongoRepository<Campaign, String> {
	public Campaign findByCampaignId(Long campaignId);
}
