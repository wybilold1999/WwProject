package com.cyanbirds.wwjy.utils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @Description:处理链接工具类
 * @author zxj
 * @Date:2015年8月12日下午3:00:34
 */
public class LinkUtil {

	public static final String URI_REGION = "((http[s]{0,1}|ftp)://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(www.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";
			/*"^(http|https|ftp)\\://([a-zA-Z0-9\\.\\-]+(\\:[a-zA-"   
	           + "Z0-9\\.&%\\$\\-]+)*@)?((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{"   
	           + "2}|[1-9]{1}[0-9]{1}|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1]{1}"   
	           + "[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|"   
	           + "[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\\.(25[0-5]|2[0-"   
	           + "4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0"   
	           + "-9\\-]+\\.)*[a-zA-Z0-9\\-]+\\.[a-zA-Z]{2,4})(\\:[0-9]+)?(/"   
	           + "[^/][a-zA-Z0-9\\.\\,\\?\\'\\\\/\\+&%\\$\\=~_\\-@]*)*$";*/
	static Pattern p = Pattern.compile(URI_REGION);
	/**
	 * 获取文本内的连接
	 * 
	 * @param content
	 * @return
	 */
	public static Set<String> getTextUrls(String content) {
		Matcher matcher = p.matcher(content);
		int startFindIndex = 0;
		Set<String> urls = null;

		while (matcher.find(startFindIndex)) {
			if (null == urls) {
				urls = new HashSet<String>();
			}
			String uriText = matcher.group();
			startFindIndex = matcher.end();
			urls.add(uriText);
		}
		return urls;
	}

	/**
	 * 处理替换链接
	 * 
	 * @param content
	 * @param urls
	 * @return
	 */
	public static String generateLink(String content, Set<String> urls) {
		String linkText = content;
		if (null == urls || urls.size() <= 0) {
			return linkText;
		}
		String rtnString = content;
		Iterator<String> uriIterator = urls.iterator();
		while(uriIterator.hasNext()){
			String uri = uriIterator.next();
			rtnString = rtnString.replace(uri, "<a href=\""+uri+"\">"+uri+"</a>");
		}
		return rtnString;
	}

	public static String checkURL(String url) {
		if (url.startsWith("HTTP://")) {
			url = "http" + url.substring(4);
		} else if (url.startsWith("HTTPS://")) {
			url = "https" + url.substring(5);
		}

		if (CheckUtil.isBasicallyValidURI(url)) {
			if (!url.contains("http://") && !url.contains("https://")) {
				url = "http://" + url;
			}
		}
		return url;
	}

}
