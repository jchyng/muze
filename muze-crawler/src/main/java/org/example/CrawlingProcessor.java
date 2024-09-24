package org.example;

import org.example.domain.Actor;
import org.example.domain.Musical;
import org.example.playdb.Genre;
import org.example.playdb.LookupType;
import org.example.playdb.PlayDBCrawler;
import org.example.playdb.PlayDBParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CrawlingProcessor {
    private static final Logger log = LoggerFactory.getLogger(CrawlingProcessor.class);
    private final PlayDBParser playDBParser = new PlayDBParser();

    public boolean crawling(LookupType lookupType, Genre[] genres) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<Future<Map<Musical, List<Actor>>>> futures = new ArrayList<>();

        for (Genre genre : genres) {
            futures.add(executor.submit(() -> crawlingProcess(lookupType, genre)));
        }

        Map<Musical, List<Actor>> totalResult = new HashMap<>();
        for (int i = 0; i < futures.size(); i++) {
            try {
                Map<Musical, List<Actor>> result = futures.get(i).get();
                totalResult.putAll(result);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error while crawling genre: {}", genres[i], e);
                return false;
            }
        }

        executor.shutdown();
        log.debug("Crawling completed for all genres. Total musicals crawled: {}", totalResult.size());
        return true;
    }

    private Map<Musical, List<Actor>> crawlingProcess(LookupType lookupType, Genre genre) {
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
