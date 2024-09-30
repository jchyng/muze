package org.muze.playdb;

import org.muze.playdb.domain.Actor;
import org.muze.playdb.domain.Casting;
import org.muze.playdb.domain.Musical;
import org.muze.playdb.dto.PlayDBResult;
import org.muze.playdb.request.Genre;
import org.muze.playdb.request.LookupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Crawler implements Callable<PlayDBResult> {

    private static final Logger log = LoggerFactory.getLogger(Crawler.class);
    private static final int MUSICALS_PER_THREAD = 1000;
    private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();

    private final Parser parser;
    private final LookupType lookupType;
    private final Genre genre;

    public Crawler(LookupType lookupType, Genre genre) {
        this.parser = new Parser();
        this.lookupType = lookupType;
        this.genre = genre;
    }

    @Override
    public PlayDBResult call() throws Exception {
        int maxPage = getMaxPage();
        if (maxPage < 1) {
            log.warn("No pages found for genre: {}", genre);
            return new PlayDBResult();
        }

        List<String> musicalIds = getAllMusicalIds(maxPage);
        return ParallelCrawling(musicalIds);
    }

    private int getMaxPage() throws IOException {
        try {
            return parser.getMaxPage(lookupType, genre);
        } catch (IOException e) {
            log.error("Failed to get max page for genre: {}", genre, e);
            throw e;
        }
    }

    private List<String> getAllMusicalIds(int maxPage) throws IOException {
        try {
            return parser.getAllMusicalIds(lookupType, genre, maxPage);
        } catch (IOException e) {
            log.error("Failed to get musical IDs for genre: {}", genre, e);
            throw e;
        }
    }

    private PlayDBResult ParallelCrawling(List<String> musicalIds) {
        int totalCount = musicalIds.size();
        int threadCount = Math.min(THREAD_POOL_SIZE, (int) Math.ceil((double) totalCount / MUSICALS_PER_THREAD));
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Future<PlayDBResult>> futures = new ArrayList<>();
        AtomicInteger processedCount = new AtomicInteger(0);

        for (int i = 0; i < totalCount; i += MUSICALS_PER_THREAD) {
            int end = Math.min(i + MUSICALS_PER_THREAD, totalCount);
            List<String> subList = musicalIds.subList(i, end);

            futures.add(executor.submit(() -> crawling(subList, processedCount, totalCount)));
        }

        PlayDBResult result = combineResults(futures);
        executor.shutdown();
        log.info("Crawling completed. Total musicals processed: {}", totalCount);
        return result;
    }

    private PlayDBResult combineResults(List<Future<PlayDBResult>> futures) {
        PlayDBResult result = new PlayDBResult();
        for (Future<PlayDBResult> future : futures) {
            try {
                result.union(future.get());
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error while getting result from future", e);
            }
        }
        return result;
    }

    private PlayDBResult crawling(List<String> musicalIds,
                                              AtomicInteger processedCount, int totalCount) {
        PlayDBResult result = new PlayDBResult();

        for (String musicalId : musicalIds) {
            try {
                Musical musical = parser.getMusical(musicalId);
                List<Actor> actors = parser.getActors(musicalId);

                result.addMusical(musical);
                result.addActors(actors);
                actors.forEach(actor -> {
                    Casting casting = new Casting(musical, actor, actor.getRole());
                    result.addCasting(casting);
                });

                updateProgress(processedCount, totalCount);
            } catch (IOException | ParseException e) {
                log.error("Error processing musical ID: {}", musicalId, e);
            }
        }
        return result;
    }

    private void updateProgress(AtomicInteger processedCount, int totalCount) {
        int currentCount = processedCount.incrementAndGet();
        if (currentCount % 100 == 0 || currentCount == totalCount) {
            int progress = currentCount * 100 / totalCount;
            log.debug("Progress: {}% ({}/{})", progress, currentCount, totalCount);
        }
    }
}