package com.db.logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Logger {
    private final String tableName = "logs";
    private Connection connection;

    Logger(Connection connection) throws SQLException {
        this.connection = connection;

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = '" + this.tableName + "');");
        boolean tableExists = rs.next() && rs.getBoolean(1);

        if(!tableExists) {
            createLogTable();
        }

        statement.close();
    }

    private void createLogTable() throws SQLException {
        Statement statement = this.connection.createStatement();
        String sql = "CREATE TABLE " + this.tableName + " (ID SERIAL PRIMARY KEY, Action VARCHAR(255), date TIMESTAMP DEFAULT CURRENT_TIMESTAMP , Description VARCHAR(255))";
        statement.executeUpdate(sql);
        statement.close();
    }

    public void log(String action, String description) throws SQLException {
        Statement statement = this.connection.createStatement();
        String sql = "INSERT INTO " + this.tableName + " (action, date, description) VALUES ('" + action + "', CURRENT_TIMESTAMP, '" + description + "');";
        statement.executeUpdate(sql);
        statement.close();
    }

}
