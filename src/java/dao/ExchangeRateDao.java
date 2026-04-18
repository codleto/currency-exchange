package dao;
import entity.Currency;
import entity.ExchangeRate;
import exception.ConflictException;
import exception.DatabaseException;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao {
    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();

    private static final String FIND_ALL_SQL = """
            SELECT er.id,
                   er.rate,
            
                   bc.id AS base_id,
                   bc.code AS base_code,
                   bc.full_name AS base_full_name,
                   bc.sign AS base_sign,
            
                   tc.id AS target_id,
                   tc.code AS target_code,
                   tc.full_name AS target_full_name,
                   tc.sign AS target_sign
        
            FROM exchange_rate er
            INNER JOIN currency bc
                ON er.base_currency_id = bc.id
            INNER JOIN currency tc
            ON er.target_currency_id = tc.id
            """;

    private static final String FIND_CODE = FIND_ALL_SQL + """
            WHERE bc.code = ? AND tc.code = ?
            """;

    private static final String UPDATE_SQL = """
            UPDATE exchange_rate
            SET rate = ?
            WHERE id = ?
            """;

    private static final String NEW_EXCHANGE_RATES_SQL = """
            INSERT INTO exchange_rate(base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?);
            """;

    public void updateRate(ExchangeRate newRate){
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {
            preparedStatement.setBigDecimal(1, newRate.getRate());
            preparedStatement.setInt(2, newRate.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка базы данных", e);
        }
    }

    public ExchangeRate save(ExchangeRate exchangeRate){
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(NEW_EXCHANGE_RATES_SQL, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, exchangeRate.getBaseCurrencyId().getId());
            preparedStatement.setInt(2, exchangeRate.getTargetCurrencyId().getId());
            preparedStatement.setBigDecimal(3, exchangeRate.getRate());

            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                exchangeRate.setId(generatedKeys.getInt(1));
            }

            return exchangeRate;
        } catch (SQLException e) {
            if(e.getMessage() != null && e.getMessage().contains("UNIQUE")){
                throw new ConflictException("Такая валютная пара уже существует");
            }
            throw new DatabaseException("Ошибка базы данных", e);
        }
    }

    public Optional<ExchangeRate> findByCode(String baseCode, String targetCode){
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_CODE)) {
            preparedStatement.setString(1, baseCode);
            preparedStatement.setString(2, targetCode);

            ResultSet resultSet = preparedStatement.executeQuery();

            ExchangeRate exchangeRate = null;
            if(resultSet.next()){
                exchangeRate = buildExchange(resultSet);
            }
            return Optional.ofNullable(exchangeRate);
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка базы данных", e);
        }
    }

    public List<ExchangeRate> findAll(){
        try (Connection connection = ConnectionManager.open();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            List<ExchangeRate> exchangeRates = new ArrayList<>();

            while (resultSet.next()){
                exchangeRates.add(buildExchange(resultSet));
            }

            return exchangeRates;
        } catch (SQLException e) {
            throw new DatabaseException("Ошибка базы данных", e);
        }
    }

    private static ExchangeRate buildExchange(ResultSet resultSet) throws SQLException {
        Currency baseCurrency = new Currency(
                resultSet.getInt("base_id"),
                resultSet.getString("base_code"),
                resultSet.getString("base_full_name"),
                resultSet.getString("base_sign")
        );

        Currency targetCurrency = new Currency(
                resultSet.getInt("target_id"),
                resultSet.getString("target_code"),
                resultSet.getString("target_full_name"),
                resultSet.getString("target_sign")
        );

        return new ExchangeRate(
                resultSet.getInt("id"),
                baseCurrency,
                targetCurrency,
                resultSet.getBigDecimal("rate")
        );
    }

    public static ExchangeRateDao getInstance(){
        return INSTANCE;
    }
}
