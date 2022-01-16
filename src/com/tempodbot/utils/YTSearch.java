package com.tempodbot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tempodbot.mediaqueue.MediaItem;
import com.tempodbot.mediaqueue.MediaItemType;
import com.tempodbot.mediaqueue.MediaQueue;

public class YTSearch {
	/**
	 * 
	 * @param rd read webpage
	 * @return webpage html
	 * @throws IOException
	 */
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * 
	 * @param url
	 * @return html text
	 * @throws IOException
	 */
	private static String readJsonFromUrl(String url) throws IOException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			// JSONObject json = new JSONObject(jsonText);
			return jsonText;
		} finally {
			is.close();
		}
	}

	/**
	 * 
	 * @param searchStr
	 * @return get x-length yt video link
	 */
	public static String get1YTLink(String searchStr) {
		//String YTSearch = "https://www.youtube.com/results?q=" + searchStr; //works
		String YTSearch = "https://www.youtube.com/results?search_query="+searchStr;
		String json;
		String str;
		try {
			json = readJsonFromUrl(YTSearch);

			int indexStartYTData = json.indexOf("ytInitialData");
			int indexEndYTData = json.substring(indexStartYTData).indexOf("</script>");
			String rawData = json.substring(indexStartYTData, indexEndYTData);
			Pattern cmp = Pattern.compile("\\{\"videoId\":\"(.*?)\"");
			Matcher mch = cmp.matcher(rawData);
			mch.find();
			str = mch.group();
			str = str.replaceAll("\"", "");
			str = str.substring(str.indexOf(":") + 1);
			
			
			
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			return null;
		}
		return str;
	}

	/**
	 * 
	 * @param searchStr str to search
	 * @param nr        nr of videos to search MAX 10
	 * @return list of yt videos
	 */
	public static List<String> getYTLinks(String searchStr, int nr) {

		if (nr > 10) {
			nr = 10;
			System.out.println("max 10 records");
		}
	//	String YTSearch = "https://www.youtube.com/results?q=" + searchStr.trim().replaceAll("(\\s+)", "+");
		String YTSearch = "https://www.youtube.com/results?search_query=" + searchStr.trim().replaceAll("(\\s+)", "+");
		System.out.println(YTSearch);
		List<String> ytIDList = new LinkedList<>();
		String json;
		String str;
		try {
			json = readJsonFromUrl(YTSearch);

			Pattern cmp = Pattern.compile("(videoId\\\"(.*?))([a-zA-Z0-9_-]{11})");
			Matcher mch = cmp.matcher(json);

			while (ytIDList.size() < nr) {
				mch.find();
				str = mch.group(3);
				if (!ytIDList.contains(str)) {
					ytIDList.add(str);
				}
			}

			System.out.println(ytIDList);

		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			return null;
		}
		return ytIDList;
	}
	
	
public static MediaQueue getVideoDetails(String ytLink) {
		MediaQueue queue = new MediaQueue();
			try {
				String json = readJsonFromUrl(ytLink);
						

				Pattern cmp = Pattern.compile("(videoDetails(.*?)(playerConfig))");
				Matcher mch = cmp.matcher(json);
				mch.find();
				String str = mch.group();

				Pattern ptitle = Pattern.compile("(title\\\":\\\"(.*?)\\\"([^\\\"]*)\\\")");//   (title\":\{\"\w+\":\"(.*?)\"\})

				Matcher mmatcher = ptitle.matcher(str);
				mmatcher.find();
				String title = mmatcher.group(2);
				System.out.println("title: " + title);

				Pattern pduration = Pattern.compile("(lengthSeconds\\\":\\\"(.*?)\\\"([^\\\"]*)\\\")");

				Matcher mduration = pduration.matcher(str);
				mduration.find();
				String duration = mduration.group(2);
				System.out.println("duration: " +Utils.getReadableTime(duration) + " seconds");

				Pattern pshortDescription = Pattern.compile("(shortDescription\\\":\\\"(.*?)\\\"([^\\\"]*)\\\")");

				Matcher mshortDescription = pshortDescription.matcher(str);
				mshortDescription.find();
				String description = mshortDescription.group(2);
				System.out.println("description: " + description);

				Pattern pthumbnail = Pattern.compile("(url\\\":\\\"(.*?)\\\"([^\\\"]*)\\\")");

				Matcher mthumbnail = pthumbnail.matcher(str);
				mthumbnail.find();
				String thumbnail = mthumbnail.group(2);
				System.out.println("thumbnail: " + thumbnail);

				Pattern pauthor = Pattern.compile("(author\\\":\\\"(.*?)\\\"([^\\\"]*)\\\")");

				Matcher mauthor = pauthor.matcher(str);
				mauthor.find();
				String author = mauthor.group(2);
				System.out.println("author: " + author);

				Pattern pisLive = Pattern.compile("(isLiveContent\\\":+(\\w+))");

				Matcher misLive = pisLive.matcher(str);
				misLive.find();
				String isLive = misLive.group(2);
				System.out.println("isLive:" + isLive);
				
				MediaItem item =  new MediaItem(MediaItemType.YOUTUBE,ytLink ,"requestor" , title, Utils.getReadableTime(duration), Boolean.valueOf(isLive), description,author,thumbnail);
				queue.add(item);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return queue;
	}
	
	
	
	
	

	public static MediaQueue getVideoDetails(List<String> links) {
		
		MediaQueue queue = new MediaQueue();
		for (int i = 0; i < links.size(); i++) {
			try {
				String yt = "https://www.youtube.com/watch?v=" + links.get(i);
				String json = readJsonFromUrl(yt);
				Pattern cmp = Pattern.compile("(videoDetails(.*?)(annotations))");
				Matcher mch = cmp.matcher(json);
				mch.find();
				String str = mch.group();
				// System.out.println(str);

				Pattern ptitle = Pattern.compile("(title\\\":\\\"(.*?)\\\"([^\\\"]*)\\\")");

				Matcher mmatcher = ptitle.matcher(str);
				mmatcher.find();
				String title = mmatcher.group(2);
				System.out.println("title: " + title);

				Pattern pduration = Pattern.compile("(lengthSeconds\\\":\\\"(.*?)\\\"([^\\\"]*)\\\")");

				Matcher mduration = pduration.matcher(str);
				mduration.find();
				String duration = mduration.group(2);
				System.out.println("duration: " +Utils.getReadableTime(duration) + " seconds");

				Pattern pshortDescription = Pattern.compile("(shortDescription\\\":\\\"(.*?)\\\"([^\\\"]*)\\\")");

				Matcher mshortDescription = pshortDescription.matcher(str);
				mshortDescription.find();
				String description = mshortDescription.group(2);
				System.out.println("description: " + description);

				Pattern pthumbnail = Pattern.compile("(url\\\":\\\"(.*?)\\\"([^\\\"]*)\\\")");

				Matcher mthumbnail = pthumbnail.matcher(str);
				mthumbnail.find();
				String thumbnail = mthumbnail.group(2);
				System.out.println("thumbnail: " + thumbnail);

				Pattern pauthor = Pattern.compile("(author\\\":\\\"(.*?)\\\"([^\\\"]*)\\\")");

				Matcher mauthor = pauthor.matcher(str);
				mauthor.find();
				String author = mauthor.group(2);
				System.out.println("author: " + author);

				Pattern pisLive = Pattern.compile("(isLiveContent\\\":+(\\w+))");

				Matcher misLive = pisLive.matcher(str);
				misLive.find();
				String isLive = misLive.group(2);
				System.out.println("isLive:" + isLive);
				
				MediaItem item =  new MediaItem(MediaItemType.YOUTUBE,yt ,"requestor" , title,Utils.getReadableTime(duration), Boolean.valueOf(isLive), description,author,thumbnail);
				queue.add(item);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return queue;
	}
	
	
	

}
