package org.mongo.domain;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * base entity class for campaign
 * @author rusev
 *
 */
@Document(collection="campaign")
public class Campaign extends BaseMongoObject implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -901554024642240179L;
	private String title;
	private String description;
	private String date;
	private String sum;
	private String text;
	private Long campaignId;
	private String smallImageUrl;
	private String bigImageUrl;
	private String campaignUrl;
	private Integer type;
	private String longDescription;
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getSum() {
		return sum;
	}
	public void setSum(String sum) {
		this.sum = sum;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSmallImageUrl() {
		return smallImageUrl;
	}
	public void setSmallImageUrl(String smallImageUrl) {
		this.smallImageUrl = smallImageUrl;
	}
	public String getBigImageUrl() {
		return bigImageUrl;
	}
	public void setBigImageUrl(String bigImageUrl) {
		this.bigImageUrl = bigImageUrl;
	}
	public String getCampaignUrl() {
		return campaignUrl;
	}
	public void setCampaignUrl(String campaignUrl) {
		this.campaignUrl = campaignUrl;
	}
	public Long getCampaignId() {
		return campaignId;
	}
	public void setCampaignId(Long campaignId) {
		this.campaignId = campaignId;
	}
	@Override
	public String toString() {
		return "Campaign [title=" + title + ", description=" + description
				+ ", date=" + date + ", sum=" + sum + ", text=" + text
				+ ", campaignId=" + campaignId + ", smallImageUrl="
				+ smallImageUrl + ", bigImageUrl=" + bigImageUrl
				+ ", campaignUrl=" + campaignUrl + "]";
	}
	public String getLongDescription() {
		return longDescription;
	}
	public void setLongDescription(String longDescription) {
		this.longDescription = longDescription;
	}

}
