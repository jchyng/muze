package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;

import com.sun.net.httpserver.HttpServer;
import org.example.domain.Actor;
import org.example.domain.Musical;
import org.example.playdb.Genre;
import org.example.playdb.LookupType;
import org.example.playdb.PlayDBCrawler;
import org.example.playdb.PlayDBParser;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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