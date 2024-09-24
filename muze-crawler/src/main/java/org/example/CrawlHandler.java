package org.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.example.playdb.Genre;
import org.example.playdb.LookupType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;

public class CrawlHandler implements HttpHandler {
    private final CrawlingProcessor crawlingProcessor;

    public CrawlHandler(CrawlingProcessor crawlingProcessor) {
        this.crawlingProcessor = crawlingProcessor;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        if ("POST".equalsIgnoreCase(method)) {
            // 헤더 설정 (SSE 응답)
            exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
            exchange.getResponseHeaders().set("Cache-Control", "no-cache");
            exchange.getResponseHeaders().set("Connection", "keep-alive");
            exchange.sendResponseHeaders(200, 0);

            // 응답 스트림 설정
            OutputStream outputStream = exchange.getResponseBody();

            // 클라이언트에 즉시 응답: 크롤링 작업 시작
            sendSSE(outputStream, "Crawling started");

            // 요청 파라미터로부터 genres 데이터 파싱 (예: JSON에서 추출)
            Genre[] genres = parseGenresFromRequest(exchange);

            // 비동기 크롤링 작업 시작
            CompletableFuture.runAsync(() -> {
                boolean isSuccess = crawlingProcessor.crawling(LookupType.ALL, genres);

                // 크롤링 작업이 끝나면 완료 메시지 전송
                if (isSuccess) {
                    sendSSE(outputStream, "Crawling completed successfully");
                } else {
                    sendSSE(outputStream, "Crawling failed");
                }

                // 작업이 끝났으므로 스트림 닫기
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        } else {
            exchange.sendResponseHeaders(405, -1); // 405 Method Not Allowed
        }
    }

    // SSE 메시지 전송 메서드
    private void sendSSE(OutputStream outputStream, String message) {
        try {
            outputStream.write(("data: " + message + "\n\n").getBytes());
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 요청에서 genres 추출 (예시)
    private Genre[] parseGenresFromRequest(HttpExchange exchange) {
        // JSON 파싱 등 구현 필요
        return new Genre[]{Genre.LICENSE, Genre.ORIGINAL};  // 임시로 반환
    }
}
