package org.muze;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.muze.domain.Actor;
import org.muze.domain.Musical;
import org.muze.playdb.Genre;
import org.muze.playdb.LookupType;
import org.muze.playdb.PlayDBCrawler;
import org.muze.playdb.PlayDBParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlingProcessor {

    private static final Logger log = LoggerFactory.getLogger(CrawlingProcessor.class);
    private static final CrawlingProcessor INSTANCE = new CrawlingProcessor();
    private final PlayDBParser playDBParser;


    private CrawlingProcessor() {
        this.playDBParser = PlayDBParser.getInstance();
    }

    public static CrawlingProcessor getInstance() {
        return INSTANCE;
    }


    public void start(LookupType lookupType, Genre[] genres) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(genres.length);
        List<Future<Map<Musical, List<Actor>>>> futures = new ArrayList<>();

        for (Genre genre : genres) {
            futures.add(executor.submit(() -> callCrawler(lookupType, genre)));
        }

        Map<Musical, List<Actor>> totalResult = new HashMap<>();
        for (int i = 0; i < futures.size(); i++) {
            try {
                Map<Musical, List<Actor>> result = futures.get(i).get();
                totalResult.putAll(result);
            } catch (InterruptedException | ExecutionException e) {
                throw new Exception("Error while crawling genre: " + genres[i], e);
            }
        }

        executor.shutdown();
        log.info("Crawling completed for all genres. Total musical count: {}", totalResult.size());
    }

    private Map<Musical, List<Actor>> callCrawler(LookupType lookupType, Genre genre) {
        log.info("Starting crawling process for genre: {}", genre);
        PlayDBCrawler crawler = new PlayDBCrawler(playDBParser, lookupType, genre);
        try {
            return crawler.call();
        } catch (Exception e) {
            log.error("Error in crawling process for genre: {}", genre, e);
            return new HashMap<>();
        }
    }
}
