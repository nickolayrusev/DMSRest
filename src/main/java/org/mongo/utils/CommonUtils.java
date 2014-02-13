package org.mongo.utils;

import static org.springframework.util.Assert.notNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.mongo.domain.Campaign;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtils {
	private static final Logger logger = LoggerFactory.getLogger(CommonUtils.class);
	/**
	 * Parsing sum correctly because sometimes is in format: 
	 "25 000 е вро"
	 "25 000 лева"
	 * @param sum
	 * @return
	 */
	public static final String parseSum(String sum){
		if(StringUtils.isBlank(sum)){
			return StringUtils.EMPTY;
		}
		sum = sum.replace(".", StringUtils.EMPTY) ;
		for (int i = sum.length()-1; i >= 0 ; i--) {
			if(Character.isDigit(sum.charAt(i))){
				String letters = sum.substring(i+1,sum.length()).replace(" ",	StringUtils.EMPTY);
				String digits = sum.substring(0,i+1);
				return digits + " " +letters;
			}
		}
		return StringUtils.EMPTY;
	}
	/**
	 * Parse query string and returns value by key
	 * @param queryString
	 * @param key
	 * @return the value, or empty string if not found
	 */
	public static final String parseQueryString(String queryString,String key) {
		List<NameValuePair> lstValues = URLEncodedUtils.parse(queryString, Charset.forName("UTF-8"));
		for (NameValuePair nameValuePair : lstValues) {
			if(nameValuePair.getName().equalsIgnoreCase(key)){
				return nameValuePair.getValue();
			}
		}
		return StringUtils.EMPTY;
	}
	public static String parseCampaignLongDescription(String url){
		notNull(url, "url is required");
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
			logger.info("hitting url: " + doc.baseUri());
		} catch (IOException e) {
			logger.error("io exc",e);
			return StringUtils.EMPTY;
		}
		StringBuilder builder = new StringBuilder();
		Elements paragraphs = doc.select("p");
		for(int i = 0;i<paragraphs.size();i++){
			if(i==0 || i==1 || i==2)
				continue;
			builder.append(paragraphs.get(i).text());
		}
		System.out.println(builder.toString());
		return builder.toString();
	}
	/**
	 * spage (type of campaign) :
	 * 0 - people ; 1 - organization ; 2 - other
	 * @param page
	 * @param type
	 * @return list of campaigns
	 */
	public static final List<Campaign> parseCampaignByPage(Integer page,Integer type){
		type = type==null ? 0 : type;
		page = page==null ? 0 : page;
		List<Campaign> lstCampaigns = new ArrayList<Campaign>(); 
		
		Document doc = null;
		try {
			doc = Jsoup.connect(Constants.WEBSITE + "index.php?page=4&spage="+String.valueOf(type)+"&p="+String.valueOf(page)).get();
			logger.info("hitting url: " + doc.baseUri());
		} catch (IOException e) {
			logger.error("io exc",e);
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
			campaign.setCampaignId((Long.parseLong(item)));
			campaign.setTitle(titleText.split(" - ")[0]);
			campaign.setText(dmsText);
			campaign.setDescription(anonceText);
			campaign.setSum(parseSum(sumText));
			campaign.setDate(dateText);
			campaign.setSmallImageUrl(imgUrl);
			campaign.setBigImageUrl(imgUrl.replace("file1", "file2"));
			campaign.setCampaignUrl(Constants.WEBSITE+hrefAttribute);
			campaign.setType(type);
			campaign.setLongDescription(parseCampaignLongDescription(Constants.WEBSITE+hrefAttribute));
			lstCampaigns.add(campaign);
		}
		
		return lstCampaigns;
	}
	
	public static String normalizeSpaces(String inputString){
		//(?<! [a-z]| [a-z]{2})(\.|\?|\!)(?! |\d|\.)
		//http://regex101.com/r/zE4pI4#python
		if(inputString==null)
			throw new IllegalArgumentException();
		Pattern pattern = Pattern.compile("(?<! [a-z]| [a-z]{2})[\\.|,|\\?|\\!](?! |\\d|\\.)");
		Matcher matcher = pattern.matcher(inputString);
		StringBuffer builder=new StringBuffer();
		while (matcher.find()) {
		    int s = matcher.start();
		    matcher.appendReplacement(builder, inputString.charAt(s)+" ");
		}
		matcher.appendTail(builder);
		return builder.toString();
	}
	
	public static List<Campaign> parseAllCampaigns(){
		List<Campaign> campaigns = new ArrayList<Campaign>();
		int i = 0,j = 0;
		do {
			List<Campaign> camps = parseCampaignByPage(i, j);
			campaigns.addAll(camps);
			
			if(!camps.isEmpty()){
				i++;
			}else{
				i=0;
				j++;
			}
				
		} while (j!=3);
		
		return campaigns;
	}
}
