package com.db.logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PostgresqlDB {
    private Logger logger;
    private Connection connection;

    PostgresqlDB() throws SQLException {
        DriverManager.registerDriver(new org.postgresql.Driver());

        String url = "jdbc:postgresql://localhost:5432/logger";
        String username = "postgres";
        String password = "admin";

        this.connection = DriverManager.getConnection(url, username, password);
        this.logger = new Logger(this.connection);

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT EXISTS (SELECT 1 FROM pg_tables WHERE tablename = 'users');");

        boolean tableExists = rs.next() && rs.getBoolean(1);

        if(!tableExists) {
            createUserTable();
        }
        statement.close();
    }

    private void createUserTable() throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "CREATE TABLE users (ID SERIAL PRIMARY KEY, name VARCHAR(255), age INT, favorite_color VARCHAR(255))";
        statement.executeUpdate(sql);
        statement.close();
    }

    private boolean userExists(Integer id) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery("SELECT 1 FROM users WHERE id = " + id + ";");
        boolean userExists = rs.next();
        statement.close();

        return userExists;
    }

    public void addUser(String name, Integer age, String favoriteColor) throws SQLException {

        String sql = "INSERT INTO users (name, age, favorite_color) VALUES (?, ?, ?);";
//        String sql = "INSERT INTO users (name, age, favorite_color) VALUES ('" + name + "'," + age + ",'" + favoriteColor + "');";
        PreparedStatement prepare = connection.prepareStatement(sql);

        prepare.setString(1, name);
        prepare.setInt(2, age);
        prepare.setString(3, favoriteColor);

        prepare.executeUpdate();
        prepare.close();

        logger.log("ADD", "Added new user [" + name + "]");
    }

    public void changeUserName(Integer id, String name) throws Exception {
        if(!userExists(id)) {
            throw new Exception("User with id-" + id + " doesn`t found.");
        }

        String sql = "UPDATE users SET name = ? WHERE id = ?;";
        PreparedStatement prepare = connection.prepareStatement(sql);

        prepare.setString(1, name);
        prepare.setInt(2, id);

        prepare.executeUpdate();
        prepare.close();

        logger.log("UPDATE", "Updated name for user [" + id + "] to [" + name + "]");
    }

    public void changeUserAge(Integer id, Integer age) throws Exception {
        if(!userExists(id)) {
            throw new Exception("User with id-" + id + " doesn`t found.");
        }

        String sql = "UPDATE users SET age = ? WHERE id = ?;";
        PreparedStatement prepare = connection.prepareStatement(sql);

        prepare.setInt(1, age);
        prepare.setInt(2, id);

        prepare.executeUpdate();
        prepare.close();

        logger.log("UPDATE", "Updated age for user [" + id + "] to [" + age + "]");
    }

    public void changeUserFavoriteColor(Integer id, String favoriteColor) throws Exception {
        if(!userExists(id)) {
            throw new Exception("User with id-" + id + " doesn`t found.");
        }

        String sql = "UPDATE users SET favorite_color = ? WHERE id = ?;";
        PreparedStatement prepare = connection.prepareStatement(sql);

        prepare.setString(1, favoriteColor);
        prepare.setInt(2, id);

        prepare.executeUpdate();
        prepare.close();

        logger.log("UPDATE", "Updated favorite color for user [" + id + "] to [" + favoriteColor + "]");
    }

    public void deleteUser(Integer id) throws Exception {
        if(!userExists(id)) {
            throw new Exception("User with id-" + id + " doesn`t found.");
        }

        String sql = "DELETE FROM users WHERE id = ?;";
        PreparedStatement prepare = connection.prepareStatement(sql);

        prepare.setInt(1, id);

        prepare.executeUpdate();
        prepare.close();

        logger.log("DELETE", "Deleted user [" + id + "]");
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList();

        Statement statement = connection.createStatement();
        String sql = "SELECT * FROM users";
        ResultSet set = statement.executeQuery(sql);
        while (set.next()) {
            users.add(new User(set.getInt("id"), set.getString("name"), set.getInt("age"), set.getString("favorite_color")));
        }
        statement.close();

        return users;
    }

    public List<Log> getLogs() throws SQLException {
        List<Log> logs = new ArrayList();

        Statement statement = connection.createStatement();
        String sql = "SELECT * FROM logs";
        ResultSet set = statement.executeQuery(sql);
        while (set.next()) {
            logs.add(new Log(set.getInt("id"), set.getString("action"), set.getTimestamp("date"), set.getString("description")));
        }
        statement.close();

        return logs;
    }
}
