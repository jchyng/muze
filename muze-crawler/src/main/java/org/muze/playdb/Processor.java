package org.muze.playdb;

import org.muze.playdb.db.SQLExecutor;
import org.muze.playdb.dto.PlayDBResult;
import org.muze.playdb.request.Genre;
import org.muze.playdb.request.LookupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Processor {

    private static final Logger log = LoggerFactory.getLogger(Processor.class);
    private static final int THREAD_POOL_SIZE = 10; // 스레드 풀 크기를 상수로 정의

    public void start(LookupType lookupType, Genre[] genres) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(genres.length, THREAD_POOL_SIZE));
        List<Future<PlayDBResult>> futures = new ArrayList<>();

        for (Genre genre : genres) {
            futures.add(executor.submit(() -> callCrawler(lookupType, genre)));
        }

        PlayDBResult playDBResult = aggregateData(futures, genres);

        saveToDatabase(playDBResult);

        executor.shutdown();
        log.info("Crawling completed for all genres. Total musical count: {}", playDBResult.getMusicals().size());
    }

    private PlayDBResult aggregateData(List<Future<PlayDBResult>> futures, Genre[] genres) throws Exception {
        PlayDBResult totalResult = new PlayDBResult();
        for (int i = 0; i < futures.size(); i++) {
            try {
                PlayDBResult result = futures.get(i).get();
                if (result != null) {
                    totalResult.union(result);
                } else {
                    log.warn("Null result returned for genre: {}", genres[i]);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new Exception("Error while crawling genre: " + genres[i], e);
            }
        }
        return totalResult;
    }

    private void saveToDatabase(PlayDBResult totalResult) throws SQLException {
        try (SQLExecutor sqlExecutor = new SQLExecutor()) {
            Connection connection = sqlExecutor.getConnection();
            try {
                connection.setAutoCommit(false);  // 자동 커밋 비활성화

                sqlExecutor.insertAllMusical(totalResult.getMusicals());
                sqlExecutor.insertAllActor(totalResult.getActors());
                sqlExecutor.insertAllCasting(totalResult.getCastings());

                connection.commit();  // 모든 작업이 성공하면 커밋
                log.info("Successfully saved all data to database");
            } catch (SQLException e) {
                connection.rollback();  // 예외 발생 시 롤백
                log.error("Failed to save data to database. Transaction rolled back", e);
                throw new SQLException("Transaction failed, rolled back", e);
            } finally {
                connection.setAutoCommit(true);  // 자동 커밋 모드 다시 활성화
            }
        }
    }

    private PlayDBResult callCrawler(LookupType lookupType, Genre genre) {
        log.info("Starting crawling process for genre: {}", genre);
        Crawler crawler = new Crawler(lookupType, genre);
        try {
            return crawler.call();
        } catch (Exception e) {
            log.error("Error in crawling process for genre: {}", genre, e);
            return null;
        }
    }
}