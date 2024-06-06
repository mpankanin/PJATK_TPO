package org.example.tpo5_pm_s24188;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Repository {

    private final String DB_URL = "jdbc:h2:~/VehicleDB";
    private final String USER = "sa";
    private final String PASS = "";


    public Repository(){
        init();
    }

    private void init(){
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();

            String dropTableSql = "DROP TABLE IF EXISTS VEHICLES;";
            stmt.executeUpdate(dropTableSql);

            String createTableSql = "CREATE TABLE VEHICLES (" +
                    "ID INT AUTO_INCREMENT PRIMARY KEY," +
                    "TYPE VARCHAR(255)," +
                    "BRAND VARCHAR(255)," +
                    "MODEL VARCHAR(255)," +
                    "PRODUCTION_YEAR INT," +
                    "CONSUMPTION DECIMAL(5,2)" +
                    ");";
            stmt.executeUpdate(createTableSql);

            String insertDataSql = "INSERT INTO VEHICLES (TYPE, BRAND, MODEL, PRODUCTION_YEAR, CONSUMPTION) VALUES" +
                    "('car', 'Toyota', 'Corolla', 2010, 7.0)," +
                    "('car', 'Honda', 'Civic', 2012, 6.5)," +
                    "('truck', 'Ford', 'F-150', 2015, 15.0)," +
                    "('f1', 'Ferrari', 'SF90', 2021, 30.0)," +
                    "('pickup', 'Chevrolet', 'Silverado', 2018, 12.0)," +
                    "('car', 'BMW', '3 Series', 2019, 8.0)," +
                    "('truck', 'Ram', '1500', 2020, 14.0)," +
                    "('f1', 'Mercedes', 'W12', 2021, 30.0)," +
                    "('pickup', 'GMC', 'Sierra', 2017, 13.0)," +
                    "('car', 'Audi', 'A4', 2020, 7.5);";
            stmt.executeUpdate(insertDataSql);

            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getVehicles(Map<String, String> parameters) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            String sql = generateSQL(parameters);

            ResultSet rs = stmt.executeQuery(sql);
            List<String> resultList = new ArrayList<>();
            while (rs.next()) {
                resultList.add(rs.getString("TYPE") + ";"
                        + rs.getString("BRAND") + ";"
                        + rs.getString("MODEL") + ";"
                        + rs.getInt("PRODUCTION_YEAR") + ";"
                        + rs.getDouble("CONSUMPTION"));
            }
            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateSQL(Map<String, String> parameters) {
        String select = "SELECT * FROM VEHICLES";
        StringBuilder sql = new StringBuilder(select);
        boolean firstCondition = true;

        for(Map.Entry<String, String> entry : parameters.entrySet()){
            if (!entry.getValue().isEmpty()){
                if (firstCondition) {
                    sql.append(" WHERE");
                    sql.append(String.format(" (%s = '%s')", entry.getKey(), entry.getValue()));
                    firstCondition = false;
                }
                sql.append(" AND");
                sql.append(String.format(" (%s = '%s')", entry.getKey(), entry.getValue()));
            }
        }
        sql.append(";");

        return sql.toString();
    }

}
