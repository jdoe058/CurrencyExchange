package edu.zhekadoe.currencyexchange.model;

import edu.zhekadoe.currencyexchange.exception.DaoConflictException;
import edu.zhekadoe.currencyexchange.exception.DaoException;
import edu.zhekadoe.currencyexchange.exception.DaoNotFoundException;
import edu.zhekadoe.currencyexchange.utils.ConnectionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateDao {

    public static final String CURRENCY_PAIR_ALREADY_EXISTS_MESSAGE = "A currency pair with this code already exists";
    public static final String EXCHANGE_RATE_NOT_FOUND_MESSAGE = "Exchange rate for the pair not found";


    public static final String FIND_ALL_QUERY = """
            SELECT exchange_rates.id    AS id,
                base_currency_id        AS base_id,
                base.full_name          AS base_full_name,
                base.code               AS base_code,
                base.sign               AS base_sign,
                target_currency_id      AS target_id,
                target.full_name        AS target_full_name,
                target.code             AS target_code,
                target.sign             AS target_sign,
                rate                    AS rate
            FROM exchange_rates
            JOIN currencies AS base
                ON base_currency_id = base.id
            JOIN currencies AS target
                ON target_currency_id = target.id
            """;

    public static final String FIND_BY_CODES_QUERY = FIND_ALL_QUERY + """
            WHERE base.code = ? AND target.code = ?
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO exchange_rates (base_currency_id, target_currency_id, rate)
            VALUES (?, ?, ?)
            RETURNING id
            """;

    private static final String UPDATE_QUERY = """
            UPDATE exchange_rates
            SET rate = ?
            WHERE base_currency_id = ? AND target_currency_id = ?
            RETURNING id
            """;

    private static final ExchangeRateDao INSTANCE = new ExchangeRateDao();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    public static ExchangeRateDao getInstance() {
        return INSTANCE;
    }

    public ExchangeRate create(ExchangeDto dto) {
        try (var conn = ConnectionManager.get()) {
            Currency baseCurrency = currencyDao.findByCode(dto.getBaseCurrencyCode(), conn);
            Currency targetCurrency = currencyDao.findByCode(dto.getTargetCurrencyCode(), conn);

            return create(conn, baseCurrency, targetCurrency, dto.getValue());
        } catch (SQLException e) {
            if (e instanceof SQLiteException sqliteE &&
                sqliteE.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
                throw new DaoConflictException(CURRENCY_PAIR_ALREADY_EXISTS_MESSAGE);
            }
            throw new DaoException(e.getMessage());
        }
    }

    public ExchangeRate update(ExchangeDto dto) {
        try (var conn = ConnectionManager.get()) {
            Currency baseCurrency = currencyDao.findByCode(dto.getBaseCurrencyCode(), conn);
            Currency targetCurrency = currencyDao.findByCode(dto.getTargetCurrencyCode(), conn);

            return update(conn, baseCurrency, targetCurrency, dto.getValue());
        } catch (SQLException e) {
            if (e instanceof SQLiteException sqliteE &&
                sqliteE.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE) {
                throw new DaoConflictException(CURRENCY_PAIR_ALREADY_EXISTS_MESSAGE);
            }
            throw new DaoException(e.getMessage());
        }
    }

    public List<ExchangeRate> findAll() {
        try (var conn = ConnectionManager.get();
             var stmt = conn.prepareStatement(FIND_ALL_QUERY)) {
            var rs = stmt.executeQuery();
            List<ExchangeRate> result = new ArrayList<>();
            while (rs.next()) {
                result.add(buildExchangeRate(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public ExchangeRate findByCodes(FindExchangeRateDto dto) {
        try (var conn = ConnectionManager.get();
             var stmt = conn.prepareStatement(FIND_BY_CODES_QUERY)) {
            stmt.setString(1, dto.getBaseCurrencyCode());
            stmt.setString(2, dto.getTargetCurrencyCode());
            var rs = stmt.executeQuery();

            if (rs.next()) {
                return buildExchangeRate(rs);
            }
            throw new DaoNotFoundException(EXCHANGE_RATE_NOT_FOUND_MESSAGE);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    private ExchangeRate create(
            Connection conn, Currency base, Currency target, String validRateString) throws SQLException {
        BigDecimal rate = new BigDecimal(validRateString);
        try (var stmt = conn.prepareStatement(INSERT_QUERY)) {
            stmt.setLong(1, base.getId());
            stmt.setLong(2, target.getId());
            stmt.setBigDecimal(3, rate);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return ExchangeRate.of(rs.getLong("id"), base, target, rate);
        }
    }

    private ExchangeRate update(
            Connection conn, Currency base, Currency target, String validRateString) throws SQLException {
        BigDecimal rate = new BigDecimal(validRateString);
        try (var stmt = conn.prepareStatement(UPDATE_QUERY)) {
            stmt.setBigDecimal(1, rate);
            stmt.setLong(2, base.getId());
            stmt.setLong(3, target.getId());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return ExchangeRate.of(rs.getLong("id"), base, target, rate);
        }
    }

    private ExchangeRate buildExchangeRate(ResultSet rs) throws SQLException {
        return ExchangeRate.of(
                rs.getLong("id"),
                currencyDao.buildCurrency(rs, "base_"),
                currencyDao.buildCurrency(rs, "target_"),
                rs.getBigDecimal("rate"));
    }
}
