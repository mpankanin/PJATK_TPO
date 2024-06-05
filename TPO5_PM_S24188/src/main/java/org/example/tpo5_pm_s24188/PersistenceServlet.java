package org.example.tpo5_pm_s24188;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "persistenceServlet", value = "/persistence")
public class PersistenceServlet extends HttpServlet {
    private final String DB_URL = "jdbc:h2:~/VehicleDB";
    private final String USER = "sa";
    private final String PASS = "";

    private final String select = "SELECT * FROM VEHICLES";
    private final String where = " WHERE";
    private final String and = " AND";


    @Override
    public void init() throws ServletException {
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
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("TYPE", req.getParameter("type"));
        parameters.put("BRAND", req.getParameter("brand"));
        parameters.put("MODEL", req.getParameter("model"));
        parameters.put("PRODUCTION_YEAR", req.getParameter("productionYear"));
        parameters.put("CONSUMPTION", req.getParameter("fuelConsumption"));

        String sql = generateSQL(parameters);

        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);

            List<String> resultList = new ArrayList<>();
            while (rs.next()) {
                resultList.add(rs.getString("TYPE") + ";"
                + rs.getString("BRAND") + ";"
                + rs.getString("MODEL") + ";"
                + rs.getInt("PRODUCTION_YEAR") + ";"
                + rs.getDouble("CONSUMPTION"));
            }

            req.setAttribute("resultList", resultList);

            RequestDispatcher dispatcher = req.getRequestDispatcher("/hello-servlet");
            dispatcher.forward(req, resp);

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            throw new ServletException(e);
        }

    }

    private String generateSQL(Map<String, String> parameters) {
        StringBuilder sql = new StringBuilder(select);
        boolean firstCondition = true;

        for(Map.Entry<String, String> entry : parameters.entrySet()){
            if (!entry.getValue().isEmpty()){
                if (firstCondition) {
                    sql.append(where);
                    sql.append(String.format(" (%s = '%s')", entry.getKey(), entry.getValue()));
                    firstCondition = false;
                }
                sql.append(and);
                sql.append(String.format(" (%s = '%s')", entry.getKey(), entry.getValue()));
            }
        }
        sql.append(";");

        return sql.toString();
    }

}
