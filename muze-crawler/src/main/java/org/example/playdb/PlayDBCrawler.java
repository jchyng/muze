package org.example.playdb;

import org.example.domain.Actor;
import org.example.domain.Musical;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayDBCrawler implements Callable<Map<Musical, List<Actor>>> {

  private static final Logger log = LoggerFactory.getLogger(PlayDBCrawler.class);
  private static final int MUSICALS_PER_THREAD = 1000;
  private final PlayDBParser playDBParser;
  private final Genre genre;

  public PlayDBCrawler(PlayDBParser playDBParser, Genre genre) {
    this.playDBParser = playDBParser;
    this.genre = genre;
  }

  @Override
  public Map<Musical, List<Actor>> call() throws Exception {
    int maxPage = playDBParser.getMaxPage(genre);
    List<String> musicalIds = playDBParser.getAllMusicalIds(genre, maxPage);

    int threadCount = (int) Math.ceil((double) musicalIds.size() / MUSICALS_PER_THREAD);
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);

    List<Future<Map<Musical, List<Actor>>>> futures = new ArrayList<>();
    AtomicInteger processedCount = new AtomicInteger(0);
    int totalCount = musicalIds.size();

    for (int i = 0; i < musicalIds.size(); i += MUSICALS_PER_THREAD) {
      int end = Math.min(i + MUSICALS_PER_THREAD, musicalIds.size());
      List<String> subList = musicalIds.subList(i, end);

      futures.add(executor.submit(() -> processMusicalIds(subList, processedCount, totalCount)));
    }

    Map<Musical, List<Actor>> result = new HashMap<>();
    for (Future<Map<Musical, List<Actor>>> future : futures) {
      result.putAll(future.get());
    }

    executor.shutdown();
    log.info("Crawling completed. Total musicals processed: {}", totalCount);
    return result;
  }

  private Map<Musical, List<Actor>> processMusicalIds(List<String> musicalIds,
      AtomicInteger processedCount, int totalCount) throws IOException, ParseException {
    Map<Musical, List<Actor>> musicalAndActors = new HashMap<>();
    for (String musicalId : musicalIds) {
      Map<Musical, List<Actor>> crawledData = playDBParser.getMusicalAndActors(musicalId);
      musicalAndActors.putAll(crawledData);

      int currentCount = processedCount.incrementAndGet();
      if (currentCount % 100 == 0 || currentCount == totalCount) {
        double progress = (double) currentCount / totalCount * 100;
        log.info("Progress: {}% ({}/{})", String.format("%.2f", progress), currentCount,
            totalCount);
      }
    }
    return musicalAndActors;
  }
}