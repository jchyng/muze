package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import org.example.domain.Actor;
import org.example.domain.Musical;
import org.example.playdb.Genre;
import org.example.playdb.PlayDBCrawler;
import org.example.playdb.PlayDBParser;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        PlayDBParser playDBParser = new PlayDBParser();
        ExecutorService executor = Executors.newFixedThreadPool(4);

        Genre[] genres = {Genre.LICENSE, Genre.ORIGINAL, Genre.CREATIVE, Genre.MUSICAL};
        List<Future<Map<Musical, List<Actor>>>> futures = new ArrayList<>();

        for (Genre genre : genres) {
            futures.add(executor.submit(() -> crawlingProcess(playDBParser, genre)));
        }

        Map<Musical, List<Actor>> totalResult = new HashMap<>();
        int completedGenres = 0;

        for (int i = 0; i < futures.size(); i++) {
            try {
                Map<Musical, List<Actor>> result = futures.get(i).get();
                totalResult.putAll(result);
                completedGenres++;
                log.info("Completed crawling for genre: {}. Progress: {}%", genres[i], (completedGenres * 100 / genres.length));
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error while crawling genre: " + genres[i], e);
            }
        }

        executor.shutdown();
        log.debug("Crawling completed for all genres. Total musicals crawled: {}", totalResult.size());
    }

    private static Map<Musical, List<Actor>> crawlingProcess(PlayDBParser playDBParser, Genre genre) {
        log.info("Starting crawling process for genre: {}", genre);
        PlayDBCrawler crawler = new PlayDBCrawler(playDBParser, genre);
        try {
            return crawler.call();
        } catch (Exception e) {
            log.error("Error in crawling process for genre: " + genre, e);
            return new HashMap<>();
        }
    }
}