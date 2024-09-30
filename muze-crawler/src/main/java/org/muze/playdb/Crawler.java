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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Crawler implements Callable<PlayDBResult> {

    private static final Logger log = LoggerFactory.getLogger(Crawler.class);
    private static final int MUSICALS_PER_THREAD = 1000;

    private final Parser parser;
    private final LookupType lookupType;
    private final Genre genre;

    public Crawler(LookupType lookupType, Genre genre) {
        this.parser = new Parser();
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
    public PlayDBResult call() throws Exception {
        int maxPage = parser.getMaxPage(lookupType, genre);

        if(maxPage < 1){
            return null;
        }

        List<String> musicalIds = parser.getAllMusicalIds(lookupType, genre, maxPage);

        int threadCount = (int) Math.ceil((double) musicalIds.size() / MUSICALS_PER_THREAD);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        List<Future<PlayDBResult>> futures = new ArrayList<>();
        AtomicInteger processedCount = new AtomicInteger(0);
        int totalCount = musicalIds.size();

        for (int i = 0; i < totalCount; i += MUSICALS_PER_THREAD) {
            int end = Math.min(i + MUSICALS_PER_THREAD, totalCount);
            List<String> subList = musicalIds.subList(i, end);

            futures.add(
                executor.submit(() -> crawlingByMusicalIds(subList, processedCount, totalCount)));
        }

        PlayDBResult result = new PlayDBResult();
        for (Future<PlayDBResult> future : futures) {
            result.union(future.get());
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
    private PlayDBResult crawlingByMusicalIds(List<String> musicalIds,
        AtomicInteger processedCount, int totalCount) throws IOException, ParseException {
        PlayDBResult result = new PlayDBResult();

        for (String musicalId : musicalIds) {
            Musical musical = parser.getMusical(musicalId);
            List<Actor> actors = parser.getActors(musicalId);

            result.addMusical(musical);
            result.addActors(actors);
            actors.forEach(actor -> {
                Casting casting = new Casting(musical, actor, actor.getRole());
                result.addCasting(casting);
            });

            int currentCount = processedCount.incrementAndGet();
            if (currentCount % 100 == 0 || currentCount == totalCount) {
                int progress = currentCount * 100 / totalCount;
                log.debug("Progress: {}% ({}/{})", progress, currentCount, totalCount);
            }
        }
        return result;
    }
}