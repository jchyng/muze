package org.muze.playdb;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.muze.playdb.db.SQLExecutor;
import org.muze.playdb.domain.Actor;
import org.muze.playdb.domain.Musical;
import org.muze.playdb.dto.PlayDBResult;
import org.muze.playdb.request.Genre;
import org.muze.playdb.request.LookupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Processor {

    private static final Logger log = LoggerFactory.getLogger(Processor.class);
    private static final Processor INSTANCE = new Processor();

    private final Parser parser;


    private Processor() {
        this.parser = Parser.getInstance();
    }

    public static Processor getInstance() {
        return INSTANCE;
    }


    public void start(LookupType lookupType, Genre[] genres) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(genres.length);
        List<Future<PlayDBResult>> futures = new ArrayList<>();

        for (Genre genre : genres) {
            futures.add(executor.submit(() -> callCrawler(lookupType, genre)));
        }

        PlayDBResult totalResult = new PlayDBResult();
        for (int i = 0; i < futures.size(); i++) {
            try {
                PlayDBResult result = futures.get(i).get();
                totalResult.union(result);
            } catch (InterruptedException | ExecutionException e) {
                throw new Exception("Error while crawling genre: " + genres[i], e);
            }
        }

        try (SQLExecutor sqlExecutor = new SQLExecutor()) {
            Connection connection = sqlExecutor.getConnection();
            try {
                connection.setAutoCommit(false);  // 자동 커밋 비활성화

                sqlExecutor.insertAllMusical(totalResult.getMusicals());
                sqlExecutor.insertAllActor(totalResult.getActors());
                sqlExecutor.insertAllCasting(totalResult.getCastings());

                connection.commit();  // 모든 작업이 성공하면 커밋
            } catch (Exception e) {
                connection.rollback();  // 예외 발생 시 롤백
                throw new RuntimeException("Transaction failed, rolled back", e);
            } finally {
                connection.setAutoCommit(true);  // 자동 커밋 모드 다시 활성화
            }
        }

        executor.shutdown();
        log.info("Crawling completed for all genres. Total musical count: {}",
            totalResult.getMusicals().size());
    }

    private PlayDBResult callCrawler(LookupType lookupType, Genre genre) {
        log.info("Starting crawling process for genre: {}", genre);
        Crawler crawler = new Crawler(parser, lookupType, genre);
        try {
            return crawler.call();
        } catch (Exception e) {
            log.error("Error in crawling process for genre: {}", genre, e);
            return null;
        }
    }
}
