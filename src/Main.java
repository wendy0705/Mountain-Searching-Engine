import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Mountains")
public class Main extends HttpServlet{
	private static final long serialVersionUID = 1L;
	public Main() {
	        super();
	}
	public static String search(String primary,String key1,Double weight_1,String key2,Double weight_2,String key3,Double weight_3) throws IOException {
		KeywordList kl1 = new KeywordList();  // a list to store keywords and its weight
		Keyword k1 = new Keyword(key1,weight_1);
		Keyword k2 = new Keyword(key2,weight_2);
		Keyword k3 = new Keyword(key3,weight_3);
		kl1.add(k1);  // add keywords and weights to the list
		kl1.add(k2);
		kl1.add(k3);
		Double score = 0.0;
		Double totalScore = 0.0;
		String googlePriKey = URLEncoder.encode(primary,StandardCharsets.UTF_8.toString());  // percent-encoding to the primary keyword
		SortList results= GoogleSearch.buildList(googlePriKey+"+taiwan"+"+mountain");  // search the primary keyword within the range of 'taiwan' and 'mountains'
		SortList searchResults = new SortList();  // sort the resulted URL from high to low according to its score
		int resultCount = 0;

		for (SearchResult result:results) {
			if (resultCount >= 10) {  // only show the first 10 pages
				break;
			}
			try {
				for (Keyword key:kl1.getLst()) {  // get the keywords from the keyword list
					if (key.weight == 0) {
						continue;
					}
					else {
						String keyword = key.name;
				    	WordCounter counter = new WordCounter(result.getUrl());  // import the URLs into the word counter function
				    	counter.getContent();  // get the content of the page
						score = counter.countKeyword(keyword) * key.weight;  // find out the count of the keyword in the page and multiple it with 1.0/3.0/5.0 based on its importance
						totalScore += score;  // all the score to the total score of the page
					}
				}
				if(totalScore <= 0) {
					continue;
				}
				resultCount += 1;  
				SearchResult res = new SearchResult(result.getUrl(),result.getName(),totalScore);// get the search results
				searchResults.add(res); 
				System.out.println(result.getUrl());
				totalScore = 0.0;
			}
			catch(NullPointerException e){
				continue;
			}
			catch(FileNotFoundException e){
				continue;
			}
			catch(IOException e) {
				continue;
			}
		}
		searchResults.sort();
		return searchResults.output();
	}
	
	// search page display
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String requestUri = request.getRequestURI();
		PrintWriter out = response.getWriter();
		
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Mountains</title>");
		out.println("<style type='text/css'>");
		out.println("div {margin-top: 10px;margin-bottom: 10px;margin-right: 20px;}");
		out.println("</style>");
		out.println("</head>");
		
		out.println("<body>");
		out.println("<div class='form'>");
		out.println("<form action='" + requestUri + "' method='post'>");
		out.println("<br><br><br><br>");
		out.println("<table style='margin-left:450px;'>");
		out.println("<thead></thead>");
		out.println("<tbody>\n"
				+ "<tr>\n"
				+ "<img src=\"logo.png\" width=300 height=300 style='margin-left:580px;'>\n"
				+ "<td colspan=\"2\">\n"
				+ "<input type='text' name='prime' placeholder = 'Mountain Name' required style=\" font-size:18px;border-radius:10px;border: transparent 10px solid;outline:none;background-color:#61FC6A;width:500px;height:30px;\"/>\n"
				+ "</td>\n"
				+ "<td style=\"padding:20px;\">\n"
				+ "<input type='image' value=\"\" src=\"SearchIcon.jpg\" onClick=\"document.formname.submit();\" style=\"width:40px;height:40px;\"/>\n"
				+ "</td>\n"
				+ "</tr>\n"
				+ "<tr>\n"
				+ "<td>\n"
				+ "<label for=\"More Keyword\" style=\"font-size:20px;\">More Keyword</label>\n"
				+ "<div ><input type=\"text\" name=\"keyword1\" placeholder = 'Important Keyword' style=\"font-size:16px;border-radius:10px;border: transparent 10px solid;outline:none;background-color:#B2F9B6; width:500px; height:23px;\"/>\n"
				+ "<div ><input type=\"text\" name=\"keyword2\" placeholder = 'Less Important Keyword' style=\"font-size:16px;border-radius:10px;border: transparent 10px solid;outline:none;background-color:#B2F9B6; width:500px; height:23px;\"/>\n"
				+ "<div ><input type=\"text\" name=\"keyword3\" placeholder = 'Least Important Keyword' style=\"font-size:16px;border-radius:10px;border: transparent 10px solid;outline:none;background-color:#B2F9B6; width:500px; height:23px;\"/>\n"
				+ "</td>\n"
				+ "</tr>\n"
				+ "<tr>\n"
				+ "<td>\n"
				+ "</td>\n"
				+ "</tr>\n"
				+ "<tr>\n"
				+ "<td>\n"
				+ "</td>\n"
				+ "</tr>\n"
				+ "</tbody>\n"
				+ "</table>\n"
				+ "</form></div>\n");
		out.println("</body>\n"+"</html>");
		
		out.flush();
		out.close();
	}
	
	// result page display
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String priKey  = request.getParameter("prime");
		String keyone  = request.getParameter("keyword1");
		Double weight_one = 5.0;
		String keytwo;
		Double weight_two;
		String keythree;
		Double weight_three;
		if(request.getParameter("keyword2")==null) {
			keytwo  = "null";
			weight_two = 0.0;
		}
		else {
			keytwo = request.getParameter("keyword2");
			weight_two = 3.0;
		}
		if(request.getParameter("keyword3")==null) {
			keythree  = "null";
			weight_three = 0.0;
		}
		else {
			keythree = request.getParameter("keyword3");
			weight_three = 1.0;
		}
		
		PrintWriter out = response.getWriter();
		
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta charset=\"UTF-8\">");
		out.println("<title>Mountains</title>");
		out.println("<style type='text/css'>");
		out.println("div {margin-top: 10px;margin-bottom: 10px;margin-right: 20px;}");
		out.println("p.one{border-style: solid;border-width: 1px;border-radius:25px;border-color: #61FC6A;padding-left: 20px;padding-top: 7px;}");
		out.println("a.link{text-decoration:none;color:#000000;font-size:22px;margin-left:55px}");
		out.println("a:hover {\n" + "text-decoration:underline;");
		out.println("</style>");
		out.println("</head>");
		
		out.println("<body>");
		out.println("<table style=\"margin-left:450px;\">\n"
				+ "<thead>\n"
				+ "</thead>\n"
				+ "<tbody>\n"
				+ "<tr>\n"
				+ "<td rowspan=\"2\">\n"
				+ "</td>\n"
				+ "<td style=\"padding:30px;\">\n"
				+ "<p for=\"primeKey\" class=\"one\" style=\" font-size:17px;outline:none;background-color:#61FC6A;width:500px;height:35px;\">"+priKey+"</p>\n"
				+ "</td>\n"
				+ "</tr>\n");
		out.println("</tbody>\n"+"</table>");
		out.println(search(priKey,keyone,weight_one,keytwo,weight_two,keythree,weight_three));
		out.println("</body>");
		out.println("</html>");
		
		out.flush();
		out.close();
	}
}
