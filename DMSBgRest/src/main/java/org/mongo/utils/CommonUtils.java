package org.mongo.utils;

import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

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
}
