package com.ysy.jdbcapi.basic.tutorial.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcIdentityInsertExample {
	
	public static void main(String[] args) {
        
        // MySQL 사용 시 URL 예시: jdbc:mysql://localhost:3306/testdb
		/*
		 docker run --name some-postgres \
  			-e POSTGRES_USER=testuser \
  			-e POSTGRES_PASSWORD=testpass \
  			-e POSTGRES_DB=testdb \
  			-p 5432:5432 \
  			-d postgres:15		 
		 */
		String url = "jdbc:postgresql://127.0.0.1:5432/testdb";
		String user = "testuser";
		String password = "testpass";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            createTable(conn);

            String insertSql = "INSERT INTO users (name, email) VALUES (?, ?) RETURNING id";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, "홍길동");
                pstmt.setString(2, "hong@example.com");

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        long id = rs.getLong("id");
                        System.out.println("📌 H2에서 반환된 ID: " + id);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTable(Connection conn) throws SQLException {
    	try (Statement stmt = conn.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS users");
            stmt.execute("CREATE TABLE users (" +
                         "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                         "name VARCHAR(255), " +
                         "email VARCHAR(255))");
        }
    }

}
