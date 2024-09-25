package org.example;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // /crawl 요청을 처리할 핸들러 설정
        server.createContext("/", new CrawlHandler(new CrawlingProcessor()));

        server.setExecutor(null); // 기본 실행자 사용
        server.start();

        log.info("서버가 {} 포트에서 시작되었습니다.", PORT);
    }
}