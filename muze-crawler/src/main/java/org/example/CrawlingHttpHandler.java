package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import org.example.playdb.Genre;
import org.example.playdb.LookupType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CrawlingHttpHandler implements HttpHandler {

    private static final Logger logger = LoggerFactory.getLogger(CrawlingHttpHandler.class);
    private final CrawlingProcessor crawlingProcessor = CrawlingProcessor.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod().toUpperCase();
        String path = exchange.getRequestURI().getPath();

        try {
            // POST /crawl 요청에 대한 처리
            if ("POST".equals(method) && path.startsWith("/crawl")) {
                LookupType lookupType = getLookupTypeFromPath(path);
                Genre[] genres = parseGenresFromRequest(exchange);

                initSSEResponse(exchange);
                OutputStream outputStream = exchange.getResponseBody();
                sendSSE(outputStream, "Crawling started");

                CompletableFuture.runAsync(() -> {
                    try {
                        crawlingProcessor.start(lookupType, genres);
                        sendSSE(outputStream, "Crawling completed successfully");
                    } catch (Exception e) {
                        sendSSE(outputStream, "Crawling failed");
                    }
                }).exceptionally(e -> {
                    logger.error("Error during crawling", e);
                    sendSSE(outputStream, "Crawling failed due to an error");
                    return null;
                }).thenRun(() -> closeOutputStream(outputStream));
            }
        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error", e);
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }


    private void initSSEResponse(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "text/event-stream");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache");
        exchange.getResponseHeaders().set("Connection", "keep-alive");
        exchange.sendResponseHeaders(200, 0);
    }

    private void sendSSE(OutputStream outputStream, String message) {
        try {
            outputStream.write(("data: " + message + "\n\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            logger.error("Error sending SSE message", e);
        }
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response)
        throws IOException {
        exchange.sendResponseHeaders(statusCode, response.length());
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }

    private void closeOutputStream(OutputStream outputStream) {
        try {
            outputStream.close();
        } catch (IOException e) {
            logger.error("Error closing output stream", e);
        }
    }


    private LookupType getLookupTypeFromPath(String path) {
        return switch (path) {
            case "/crawl/all" -> LookupType.ALL;
            case "/crawl/new" -> LookupType.NEW;
            default -> throw new IllegalArgumentException("Invalid path: " + path);
        };
    }

    private Genre[] parseGenresFromRequest(HttpExchange exchange) {
        try (BufferedReader br = new BufferedReader(
            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
            String body = br.lines().collect(java.util.stream.Collectors.joining());
            return objectMapper.readValue(body, Genre[].class);
        } catch (Exception e) {
            logger.error("Error parsing genres from request", e);
            return new Genre[0];
        }
    }
}