package org.muze.playdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Set;
import org.muze.playdb.domain.Actor;
import org.muze.playdb.domain.Casting;
import org.muze.playdb.domain.Musical;

public class SQLExecutor implements AutoCloseable {
    private Connection connection;

    public SQLExecutor() {
        this.connection = getConnection();
    }

    public Connection getConnection() {
        try {
            return DBConnector.getInstance().getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get connection", e);
        }
    }

    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void insertAllMusical(Set<Musical> musicals) {
        String sql = "INSERT INTO musicals "
            + "(id, title, theater, poster_image, st_date, ed_date, view_age, running_time, main_character) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (Musical musical : musicals) {
                stmt.setString(1, musical.getId());
                stmt.setString(2, musical.getTitle());
                stmt.setString(3, musical.getTheater());
                stmt.setString(4, musical.getPosterImage());
                stmt.setDate(5, new java.sql.Date(musical.getStDate().getTime()));
                stmt.setDate(6, new java.sql.Date(musical.getEdDate().getTime()));
                stmt.setString(7, musical.getViewAge());
                stmt.setString(8, musical.getRunningTime());
                stmt.setString(9, musical.getMainCharacter());
                stmt.addBatch();
            }
            stmt.executeBatch(); // 배치 실행으로 여러 레코드 한 번에 삽입
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert musicals", e);
        }
    }

    public void insertAllActor(Set<Actor> actors) {
        String sql = "INSERT INTO actors (id, name, profile_image) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (Actor actor : actors) {
                stmt.setString(1, actor.getId());
                stmt.setString(2, actor.getName());
                stmt.setString(3, actor.getProfileImage());
                stmt.addBatch();
            }
            stmt.executeBatch(); // 배치 실행으로 여러 레코드 한 번에 삽입
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert actors", e);
        }
    }

    public void insertAllCasting(Set<Casting> castings) {
        String sql = "INSERT INTO castings (musical_id, actor_id, role) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (Casting casting : castings) {
                stmt.setString(1, casting.getMusical().getId());
                stmt.setString(2, casting.getActor().getId());
                stmt.setString(3, casting.getRole());
                stmt.addBatch();
            }
            stmt.executeBatch(); // 배치 실행으로 여러 레코드 한 번에 삽입
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to insert castings", e);
        }
    }
}
