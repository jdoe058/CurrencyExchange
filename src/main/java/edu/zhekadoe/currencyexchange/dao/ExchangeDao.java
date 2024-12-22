package edu.zhekadoe.currencyexchange.dao;

import edu.zhekadoe.currencyexchange.dto.ConvertedDto;
import edu.zhekadoe.currencyexchange.exception.DaoException;
import edu.zhekadoe.currencyexchange.exception.DaoNotFoundException;
import edu.zhekadoe.currencyexchange.utils.ConnectionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static edu.zhekadoe.currencyexchange.dao.ExchangeRateDao.FIND_ALL_QUERY_PATTERN;
import static edu.zhekadoe.currencyexchange.dao.ExchangeRateDao.FIND_BY_CODES_QUERY;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeDao {
    private static final ExchangeDao INSTANCE = new ExchangeDao();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    public static ExchangeDao getInstance() {
        return INSTANCE;
    }

    private static final String FIND_ALL_REVERSE_QUERY = FIND_ALL_QUERY_PATTERN.formatted("round(1/rate)");
    private static final String FIND_REVERSE_QUERY = FIND_ALL_REVERSE_QUERY + """
            WHERE target.code = ? AND base.code = ?
            """;

    private static final String CROSS_CURRENCY_CODE = "RUB";

    public ConvertedDto exchange(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        try (Connection connection = ConnectionManager.get()) {
            return findDirectRate(connection, FIND_BY_CODES_QUERY, baseCurrencyCode, targetCurrencyCode, amount)
                    .or(() -> findDirectRate(connection, FIND_REVERSE_QUERY, targetCurrencyCode, baseCurrencyCode, amount))
                    .orElse(findCrossExchange(connection, baseCurrencyCode, targetCurrencyCode, amount));
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    private Optional<ConvertedDto> findDirectRate(
            Connection connection,
            String query,
            String baseCurrencyCode,
            String targetCurrencyCode,
            BigDecimal amount) {

        try (var stmt = connection.prepareStatement(query)) {
            stmt.setString(1, baseCurrencyCode);
            stmt.setString(2, targetCurrencyCode);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(ConvertedDto.of(
                        currencyDao.buildCurrency(rs, "base_"),
                        currencyDao.buildCurrency(rs, "target_"),
                        rs.getBigDecimal("rate"), amount));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public ConvertedDto findCrossExchange(Connection conn, String baseCode, String targetCode, BigDecimal amount) throws SQLException {
        var baseRate = findDirectRate(conn, FIND_BY_CODES_QUERY, CROSS_CURRENCY_CODE, baseCode, amount)
                .orElseThrow(this::currencyNotFound);
        var targetRate = findDirectRate(conn, FIND_BY_CODES_QUERY, CROSS_CURRENCY_CODE, targetCode, amount)
                .orElseThrow(this::currencyNotFound);

        return ConvertedDto.of(
                baseRate.getTargetCurrency(),
                targetRate.getTargetCurrency(),
                baseRate.getRate().divide(targetRate.getRate(), 6, RoundingMode.HALF_UP), amount);
    }

    private DaoException currencyNotFound() {
        return new DaoNotFoundException("Currency not found");
    }

}
