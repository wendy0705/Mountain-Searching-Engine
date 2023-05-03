import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GoogleSearch {
	
	public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
	private  SortList List=new SortList();
	
	public static String decodeValue(String value) {
	    try {
	        return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
	    } catch (UnsupportedEncodingException ex) {
	        throw new RuntimeException(ex.getCause());
	    }
	}
	
	
	public static SortList buildList(String pk) throws IOException{
		GoogleSearch tempR=new GoogleSearch();
		String searchTerm = pk;
		int num=200;
		
		String searchURL = GOOGLE_SEARCH_URL + "?q=" + searchTerm + "&num="+ num + "&sourceid=chrome&ie=UTF-8";
		System.out.println(searchURL);
		Document doc = Jsoup.connect(searchURL).ignoreContentType(true).userAgent("Chrome/7.0.517.44").get();
		Elements url = doc.select("div").select(".kCrYT");
		for (Element link : url) {
			try{
				String citeUrl = link.select("a").get(0).attr("href");
				String title = link.select("a").get(0).select(".vvjwJb").text();
				String decodeURL = citeUrl.split("q=")[1];
				SearchResult res = new SearchResult(decodeValue(decodeURL),title);
				tempR.List.add(res);

			} catch (IndexOutOfBoundsException e) {
				
			}
		}
		return tempR.List;
	}
}


