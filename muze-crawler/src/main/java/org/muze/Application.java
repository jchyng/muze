package org.muze;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
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
    }

    //todo: JPA 실행 테스트를 위한 JUnit 테스트 작성
}