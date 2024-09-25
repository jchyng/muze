package org.example.playdb;

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
import org.example.domain.Actor;
import org.example.domain.Musical;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlayDBCrawler implements Callable<Map<Musical, List<Actor>>> {

    private static final Logger log = LoggerFactory.getLogger(PlayDBCrawler.class);
    private static final int MUSICALS_PER_THREAD = 1000;
    private final PlayDBParser playDBParser;
    private final LookupType lookupType;
    private final Genre genre;

    public PlayDBCrawler(PlayDBParser playDBParser, LookupType lookupType, Genre genre) {
        this.playDBParser = playDBParser;
        this.lookupType = lookupType;
        this.genre = genre;
    }

    /**
     * @formatter:off
     * 1. 장르에 해당하는 페이지의 총 페이지 수를 구한다.
     * 2. 페이지를 탐색하여 모든 뮤지컬 아이디를 가져온다.
     * 3. 뮤지컬 아이디 1000개 당 스레드를 할당한다.
     * 4. 각 스레드는 뮤지컬 아이디를 기반으로 뮤지컬 정보와 배우 정보를 가져온다.
     */
    @Override
    public Map<Musical, List<Actor>> call() throws Exception {
        int maxPage = playDBParser.getMaxPage(lookupType, genre);

        if(maxPage < 1){
            return Map.of();
        }

        List<String> musicalIds = playDBParser.getAllMusicalIds(lookupType, genre, maxPage);

        int threadCount = (int) Math.ceil((double) musicalIds.size() / MUSICALS_PER_THREAD);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Future<Map<Musical, List<Actor>>>> futures = new ArrayList<>();
        AtomicInteger processedCount = new AtomicInteger(0);
        int totalCount = musicalIds.size();

        for (int i = 0; i < totalCount; i += MUSICALS_PER_THREAD) {
            int end = Math.min(i + MUSICALS_PER_THREAD, totalCount);
            List<String> subList = musicalIds.subList(i, end);

            futures.add(
                executor.submit(() -> crawlingByMusicalIds(subList, processedCount, totalCount)));
        }

        Map<Musical, List<Actor>> result = new HashMap<>();
        for (Future<Map<Musical, List<Actor>>> future : futures) {
            result.putAll(future.get());
        }

        executor.shutdown();
        log.info("Crawling completed. Total musicals processed: {}", totalCount);
        return result;
    }

    /**
     * @Param musicalIds: 뮤지컬 아이디 목록
     * @Param processedCount: 처리된 뮤지컬 수
     * @Param totalCount: 전체 뮤지컬 수
     * */
    private Map<Musical, List<Actor>> crawlingByMusicalIds(List<String> musicalIds,
        AtomicInteger processedCount, int totalCount) throws IOException, ParseException {
        Map<Musical, List<Actor>> musicalAndActors = new HashMap<>();
        for (String musicalId : musicalIds) {
            Map<Musical, List<Actor>> crawledData = playDBParser.getMusicalAndActors(musicalId);
            musicalAndActors.putAll(crawledData);

            int currentCount = processedCount.incrementAndGet();
            if (currentCount % 100 == 0 || currentCount == totalCount) {
                int progress = currentCount * 100 / totalCount;
                log.debug("Progress: {}% ({}/{})", progress, currentCount, totalCount);
            }
        }
        return musicalAndActors;
    }
}