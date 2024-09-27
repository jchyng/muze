package org.muze;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.muze.domain.Actor;
import org.muze.domain.Musical;
import org.muze.jpa.JpaExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final int PORT = 8081;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new CrawlingHttpHandler());
        server.start();
        log.info("Server started with {} ports.", PORT);

        JpaExecutor instance = JpaExecutor.getInstance();
        Map<Musical, List<Actor>> mus = new HashMap<>();
        mus.put(
                Musical.builder().id("1234").title("sss").build(),
                List.of(Actor.builder().id("1234").name("sss").role("xxx").build())
        );
        instance.saveMusicals(mus);
    }

    //todo: JPA 실행 테스트를 위한 JUnit 테스트 작성
}