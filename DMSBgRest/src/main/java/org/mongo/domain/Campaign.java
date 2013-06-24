package org.mongo.domain;

import java.io.Serializable;

/**
 * base entity class for campaign
 * @author rusev
 *
 */
public class Campaign implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -901554024642240179L;
	private String title;
	private String description;
	private String date;
	private String sum;
	private String text;
	private Long id;
	private String smallImageUrl;
	private String bigImageUrl;
	private String campaignUrl;
	
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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

}
