package org.muze;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;
import org.muze.playdb.Controller;
import org.muze.playdb.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);
    private static final int PORT = 8081;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        ObjectMapper mapper = new ObjectMapper();
        Processor processor = new Processor();
        Controller controller = new Controller(processor, mapper);

        server.createContext("/", controller);
        server.start();
        log.info("Server started with {} ports.", PORT);
    }
}