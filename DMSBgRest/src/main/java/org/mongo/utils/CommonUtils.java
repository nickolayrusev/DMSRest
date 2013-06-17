package org.mongo.utils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mongo.domain.Campaign;

public class CommonUtils {

	/**
	 * Parse query string and returns value by key
	 * @param queryString
	 * @param key
	 * @return the value, or empty string if not found
	 */
	public static String parseQueryString(String queryString,String key) {
		List<NameValuePair> lstValues = URLEncodedUtils.parse(queryString, Charset.forName("UTF-8"));
		for (NameValuePair nameValuePair : lstValues) {
			if(nameValuePair.getName().equalsIgnoreCase(key)){
				return nameValuePair.getValue();
			}
		}
		return StringUtils.EMPTY;
	}
	public static List<Campaign> parseCampaignByPage(Integer page,Integer type){
		List<Campaign> lstCampaigns = new ArrayList<Campaign>(); 
		Document doc = null;
		try {
			doc = Jsoup.connect("http://dmsbg.com/index.php?page=4&spage="+String.valueOf(type)+"&p="+String.valueOf(page)).get();
		} catch (IOException e) {
			return Collections.emptyList();
		}
		Elements links = doc.select("a.news_item");
		for (Element element : links) {
			String hrefAttribute = element.attr("href");
			String item = CommonUtils.parseQueryString(hrefAttribute,"item");
			Element titleElement = element.select(".title").first();
			String titleText = titleElement.text();
			String dmsText = titleElement.children().first().text();
			Element dateElement = element.select(".date").first();
			String dateText = dateElement.text();
			Element anonceElement = element.select(".anonce").first();
			String anonceText = anonceElement.text();
			Elements sumElements = anonceElement.select("strong");
			String sumText = sumElements.text();
			
			String imgUrl = element.select("img").attr("src");
			
			Campaign campaign = new Campaign();
			campaign.setId(Long.parseLong(item));
			campaign.setTitle(titleText);
			campaign.setText(dmsText);
			campaign.setDescription(anonceText);
			campaign.setSum(sumText);
			campaign.setDate(dateText);
			campaign.setSmallImageUrl(imgUrl);
			campaign.setBigImageUrl(imgUrl.replace("file1", "file2"));
			
			lstCampaigns.add(campaign);
		}
		
		return lstCampaigns;
	}
}
