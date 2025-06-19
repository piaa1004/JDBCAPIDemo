package com.ysy.jdbcapi.introduction;

import java.sql.*;

public class Main {

    private static final String URL = "jdbc:mysql://localhost:3306/testdb?serverTimezone=Asia/Seoul";
    private static Connection con;

    // 🔹 1. DB 연결 메서드 (책임 분리)
    private static Connection getConnection(String username, String password) throws SQLException {
        if (con == null || con.isClosed()) {
            con = DriverManager.getConnection(URL, username, password);
        }
        return con;
    }

    // 🔹 2. 실행 흐름 제어 메서드
    public void runQueries(String username, String password) {
        try {
            getConnection(username, password); // 연결 설정

            queryAllCoffees();
            queryCoffeesByNamePrefix("French"); 
            queryCoffeeBySupplierId(49);
            queryCoffeeBySupplierIdGreaterThan(100);
            queryCoffeesWithNullTotalAndLowSupplierId(100);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (con != null && !con.isClosed()) {
                    con.close();
                    con = null;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    // 🔹 3. 전체 커피 목록 조회
    private void queryAllCoffees() throws SQLException {
        String sql = "SELECT * FROM coffees";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("[Coffees 테이블 전체 조회 결과]");
            while (rs.next()) {
                String coffeeName = rs.getString("COF_NAME");
                int supID = rs.getInt("SUP_ID");
                float price = rs.getFloat("PRICE");
                int sales = rs.getInt("SALES");
                int total = rs.getInt("TOTAL");

                System.out.println("Coffee Name: " + coffeeName +
                        ", SUP_ID: " + supID +
                        ", Price: " + price +
                        ", Sales: " + sales +
                        ", Total: " + total);
            }
        }
    }

    // 🔹 4. 이름 Prefix로 조회
    private void queryCoffeesByNamePrefix(String prefix) throws SQLException {
        String sql = "SELECT COF_NAME, SUP_ID FROM coffees WHERE COF_NAME LIKE ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, prefix + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n[COF_NAME LIKE '" + prefix + "%' 조회 결과]");
                while (rs.next()) {
                    String cofName = rs.getString("COF_NAME");
                    int supId = rs.getInt("SUP_ID");

                    System.out.println("Coffee Name: " + cofName + ", SUP_ID: " + supId);
                }
            }
        }
    }
    
    private void queryCoffeeBySupplierId(int supId) throws SQLException {
        String sql = "SELECT COF_NAME, PRICE FROM coffees WHERE SUP_ID = ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, supId);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n[SUP_ID = " + supId + " 인 커피 조회 결과]");
                while (rs.next()) {
                    String name = rs.getString("COF_NAME");
                    float price = rs.getFloat("PRICE");

                    System.out.println("Coffee Name: " + name + ", Price: " + price);
                }
            }
        }
    }
    
    private void queryCoffeeBySupplierIdGreaterThan(int minSupId) throws SQLException {
        String sql = "SELECT COF_NAME, PRICE FROM coffees WHERE SUP_ID > ?";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, minSupId);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n[SUP_ID > " + minSupId + " 인 커피 조회 결과]");
                while (rs.next()) {
                    String name = rs.getString("COF_NAME");
                    float price = rs.getFloat("PRICE");

                    System.out.println("Coffee Name: " + name + ", Price: " + price);
                }
            }
        }
    }
    
    private void queryCoffeesWithNullTotalAndLowSupplierId(int maxSupId) throws SQLException {
        String sql = "SELECT COF_NAME, PRICE FROM coffees WHERE SUP_ID < ? AND TOTAL IS NULL";

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, maxSupId);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.println("\n[SUP_ID < " + maxSupId + " AND TOTAL IS NULL 조회 결과]");
                while (rs.next()) {
                    String name = rs.getString("COF_NAME");
                    float price = rs.getFloat("PRICE");

                    System.out.println("Coffee Name: " + name + ", Price: " + price);
                }
            }
        }
    }



    // 🔹 5. main 진입점
    public static void main(String[] args) {
        new Main().runQueries("root", "1234");
    }
}
