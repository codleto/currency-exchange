package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionManager {

    private static final String URL_KEY ="db.url";

    ConnectionManager(){}

    public static Connection open() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(PropertiesUtil.get(URL_KEY));
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

