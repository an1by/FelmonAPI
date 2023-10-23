package net.aniby.felmonapi.database;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FelmonConnection {
    protected final @NotNull String[] connectionData;
    private @Nullable Connection connection;

    public Connection getConnection() {
        return connection;
    }

    protected FelmonConnection(@NotNull String[] connectionData) throws SQLException {
        this.connectionData = connectionData;
        this.connection = connectionData.length == 3
                ? DriverManager.getConnection(connectionData[0], connectionData[1], connectionData[2])
                : null;
    }

    public static @Nullable FelmonConnection connect(String host, String database, String user, String password) {
        try {
            String url = String.format("jdbc:mysql://%s/%s", host, database);
            url += "?autoReconnect=true&initialTimeout=1&useSSL=false";
            return new FelmonConnection(new String[]{
                    url, user, password
            });

        } catch (SQLException ignored) {
            return null;
        }
    }

    public boolean reconnectIfClosed() {
        try {
            if (connection != null && this.connectionData.length == 3) {
                if (connection.isClosed())
                    this.connection = DriverManager.getConnection(connectionData[0], connectionData[1], connectionData[2]);
                return true;
            }
        } catch (SQLException ignored) {
        }
        new RuntimeException("Database reconnect error!").printStackTrace();
        return false;
    }

    public boolean disconnect() {
        if (connection != null)
            try {
                connection.close();
                return true;
            } catch(SQLException ignored) {}
        return false;
    }
}
