package dao;

import entity.Currency;
import exception.ConflictException;
import exception.DatabaseException;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDao {

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private static final String SAVE_SQL = """
            INSERT INTO currency (code, full_name, sign)
            VALUES (?, ?, ?);
          """;

    private static final String SHOW_ALL_SQL = """
                SELECT id,
                   code,
                   full_name,
                   sign
            FROM currency
            """;

    private static final String SHOW_SQL = SHOW_ALL_SQL + """
            WHERE code = ?;
            """;

    private CurrencyDao(){}

    public List<Currency> findAll(){
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(SHOW_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Currency> currencies = new ArrayList<>();

            while (resultSet.next()){
                currencies.add(buildCurrencies(resultSet));
            }
            return currencies;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка базы данных", e);
        }
    }

    public Optional<Currency> findByCode(String code){

        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(SHOW_SQL)) {
            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();

            Currency currency = null;

            if (resultSet.next()){
                currency = buildCurrencies(resultSet);
            }
            return Optional.ofNullable(currency);
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка базы данных", e);
        }
    }

    public Currency save(Currency currency) {

        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getName());
            preparedStatement.setString(3, currency.getSign());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()){
                currency.setId(generatedKeys.getInt(1));
            }
            return currency;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UNIQUE")){
                throw new ConflictException("Данная валюта уже существует");
            }
            throw new DatabaseException("Ошибка базы данных", e);
        }
    }

    private static Currency buildCurrencies(ResultSet resultSet) throws SQLException {
        return new Currency(
                resultSet.getInt("id"),
                resultSet.getString("code"),
                resultSet.getString("full_name"),
                resultSet.getString("sign")
        );
    }

    public static CurrencyDao getInstance(){
        return INSTANCE;
    }

}
