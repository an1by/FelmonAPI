package ru.aniby.felmonapi.database;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
    public static Connection connect(String host, String database, String user, String password) {
        try {
            String url = String.format("jdbc:mysql://%s/%s", host, database);
            url += "?autoReconnect=true&initialTimeout=1&useSSL=false";
            return DriverManager.getConnection(url, user, password);

        } catch (SQLException ignored) {
            return null;
        }
    }

    public static void loadDriver() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static void disconnect(@Nullable Connection connection) {
        if (connection == null)
            return;
        try { connection.close(); } catch(SQLException ignored) { /*can't do anything */ }
    }
}
