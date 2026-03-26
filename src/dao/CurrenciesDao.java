package dao;

import entity.Currencies;

import java.sql.*;

public class CurrenciesDao {

    private static final CurrenciesDao INSTANCE = new CurrenciesDao();
    private static final String URL = "jdbc:sqlite:/Users/codleto/databases/currency_exchange.db";
    private static final String SAVE_SQL = """
            INSERT INTO currencies (code, full_name, sign)
            VALUES (?, ?, ?);
            """;
    private static final String SHOW_SQL = """
            SELECT *
            FROM currencies;
            """;

    private CurrenciesDao(){};

    public void showAll(){
        try (Connection connection = DriverManager.getConnection(URL);
        PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){

            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Currencies save(Currencies currencies) {
    }

    public static CurrenciesDao getInstance(){
        return INSTANCE;
    }

}
