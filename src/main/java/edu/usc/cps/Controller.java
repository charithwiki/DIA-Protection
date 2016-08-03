package edu.usc.cps;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by charith on 7/22/16.
 */
public class Controller {

    public static void main(String[] args) throws Exception {
        String crawlStorageFolder = args[0];
        int numberOfCrawlers = 7;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setPolitenessDelay(500);
        config.setUserAgentString("USC-Browser");

        /*
         * Instantiate the controller for this crawl.
         */
        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        config.setMaxDepthOfCrawling(1);
        /*
         * For each crawl, you need to add some seed urls. These are the first
         * URLs that are fetched and then the crawler starts following links
         * which are found in these pages
         */

        BufferedReader reader = new BufferedReader(new FileReader(args[1]));
        PrintWriter writer = new PrintWriter(new FileWriter(args[2]));

        String line = reader.readLine();

        while (line != null) {

            StringTokenizer tokenizer = new StringTokenizer(line);
            String id = tokenizer.nextToken();
            String url = tokenizer.nextToken();
            //System.out.println(url);
            controller.addSeed(url);
            TestCrawler.urls.put(Integer.parseInt(id),url);
            line = reader.readLine();
        }

        System.out.println("done adding seed");
//        controller.addSeed("http://www.ics.uci.edu/~lopes/");
//        controller.addSeed("http://www.ics.uci.edu/~welling/");
//        controller.addSeed("http://www.ics.uci.edu/");
//
//        /*
//         * Start the crawl. This is a blocking operation, meaning that your code
//         * will reach the line after this only when crawling is finished.
//         */
        controller.start(TestCrawler.class, numberOfCrawlers);
        List<Object> data = controller.getCrawlersLocalData();

        Map<Integer,String> parent = new HashMap<>();

        for(Object o : data) {
            Map<Integer,String> map =  (Map<Integer,String>)o;
            parent.putAll(map);


        }

        for(Integer id:parent.keySet()) {
            writer.println("" + id + ", " + parent.get(id));
        }

        writer.flush();
        writer.close();
    }


}
