package org.muze.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnector {
    private static DBConnector instance;
    private final Properties props;
    private Connection connection;

    private DBConnector() {
        this.props = loadProperties("db.properties");
    }

    public static synchronized DBConnector getInstance() {
        if (instance == null) {
            instance = new DBConnector();
        }
        return instance;
    }

    // 프로퍼티 파일을 로드하는 메서드
    private Properties loadProperties(String properties) {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(properties)) {
            if (input != null) {
                props.load(input);
            } else {
                throw new RuntimeException("Properties file not found: " + properties);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load properties file: " + properties, e);
        }
        return props;
    }

    // 데이터베이스 연결을 가져오는 메서드
    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String password = props.getProperty("db.password");
            connection = DriverManager.getConnection(url, user, password);
        }
        return connection;
    }

    // 연결을 닫는 메서드
    public synchronized void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            connection = null; // 연결이 닫힌 후에 객체를 null로 설정
        }
    }
}
