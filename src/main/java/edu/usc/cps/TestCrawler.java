package edu.usc.cps;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by charith on 7/22/16.
 */
public class TestCrawler extends WebCrawler {

    private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg"
            + "|png|mp3|mp3|zip|gz))$");


    private static final String HTML_A_TAG_PATTERN = "(?i)<a([^>]+)>(.+?)</a>";

    public static Map<Integer,String> urls = new HashMap<Integer,String>();

    private static Map<Integer,String> localData = new HashMap<>();

    /**
     * This method receives two parameters. The first parameter is the page
     * in which we have discovered this new url and the second parameter is
     * the new url. You should implement this function to specify whether
     * the given url should be crawled or not (based on your crawling logic).
     * In this example, we are instructing the crawler to ignore urls that
     * have css, js, git, ... extensions and to only accept urls that start
     * with "http://www.ics.uci.edu/". In this case, we didn't need the
     * referringPage parameter to make the decision.
     */
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();

        return urls.containsValue(href);
    }

    /**
     * This function is called when a page is fetched and ready
     * to be processed by your program.
     */
    @Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
            String text = htmlParseData.getText();
            String html = htmlParseData.getHtml();
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            System.out.println("Text length: " + text.length());
            System.out.println("Html length: " + html.length());
            System.out.println("Number of outgoing links: " + links.size());
           // System.out.println(html);

            org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(html);
            Elements alinks = doc.select("a[href]");




            for (Element a : alinks) {
                if(a.attr("title").equals("Full text at publisher's site")) {

                    String aurl =  a.attr("abs:href");
                    String aurlParts[] = url.split("/");
                    System.out.println("" + aurlParts[aurlParts.length -1] + ", " +   aurl);
                    localData.put(Integer.parseInt(aurlParts[aurlParts.length -1]),aurl);
                }
            }




        }
    }

    @Override
    public Object getMyLocalData() {
        return localData;
    }
}
