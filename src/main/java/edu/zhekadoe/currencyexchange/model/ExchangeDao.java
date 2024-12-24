package edu.zhekadoe.currencyexchange.model;

import edu.zhekadoe.currencyexchange.exception.DaoException;
import edu.zhekadoe.currencyexchange.exception.DaoNotFoundException;
import edu.zhekadoe.currencyexchange.utils.ConnectionManager;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeDao {
    private static final String CROSS_CURRENCY_CODE = "USD";

    private static final String FIND_DIRECT = """
            SELECT
                base_currency_id        AS base_id,
                base.full_name          AS base_full_name,
                base.code               AS base_code,
                base.sign               AS base_sign,
                target_currency_id      AS target_id,
                target.full_name        AS target_full_name,
                target.code             AS target_code,
                target.sign             AS target_sign,
                rate                    AS rate,
                ?                       AS amount
            FROM exchange_rates
            JOIN currencies AS base
                ON base_currency_id = base.id
            JOIN currencies AS target
                ON target_currency_id = target.id
            WHERE base.code = ? AND target.code = ?
            """;

    private static final String FIND_REVERSE = """
            SELECT
                base_currency_id        AS base_id,
                base.full_name          AS base_full_name,
                base.code               AS base_code,
                base.sign               AS base_sign,
                target_currency_id      AS target_id,
                target.full_name        AS target_full_name,
                target.code             AS target_code,
                target.sign             AS target_sign,
                1.0/rate                AS rate,
                ?                       AS amount
            FROM exchange_rates
            JOIN currencies     AS base
                ON base_currency_id = base.id
            JOIN currencies     AS target
                ON target_currency_id = target.id
            WHERE target.code = ? AND base.code = ?
            """;

    private static final String FIND_CROSS = """
            SELECT
                base.id                 AS base_id,
                base.full_name          AS base_full_name,
                base.code               AS base_code,
                base.sign               AS base_sign,
                target.id               AS target_id,
                target.full_name        AS target_full_name,
                target.code             AS target_code,
                target.sign             AS target_sign,
                e_base.rate/e_target.rate as rate,
                ?                       AS amount
            FROM
                exchange_rates as e_base,
                exchange_rates as e_target
            JOIN currencies     as base
                ON e_base.target_currency_id = base.id
            JOIN currencies     as target
                ON e_target.target_currency_id = target.id
            JOIN currencies     as cross_
                ON e_base.base_currency_id = cross_.id
            WHERE cross_.code = ? AND base.code = ? AND target.code = ?
            """;


    private static final ExchangeDao INSTANCE = new ExchangeDao();
    private final CurrencyDao currencyDao = CurrencyDao.getInstance();

    public ConvertedDto exchange(ExchangeDto dto) {
        try (Connection connection = ConnectionManager.get()) {
            // or().orElse не работали хз почему
            Optional<ConvertedDto> directRate = findByCodes(connection, FIND_DIRECT, dto);
            if (directRate.isPresent()) {
                return directRate.get();
            }

            Optional<ConvertedDto> reverseRate = findByCodes(connection, FIND_REVERSE, dto);
            if (reverseRate.isPresent()) {
                return reverseRate.get();
            }

            return findCrossExchange(connection, dto);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public static ExchangeDao getInstance() {
        return INSTANCE;
    }

    private Optional<ConvertedDto> findByCodes(
            Connection connection,
            String query,
            @NonNull ExchangeDto dto) {

        try (var stmt = connection.prepareStatement(query)) {
            stmt.setString(1, dto.value());
            stmt.setString(2, dto.codes().base());
            stmt.setString(3, dto.codes().target());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(buildConvertedDto(rs));
            }

            return Optional.empty();
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public ConvertedDto findCrossExchange(
            Connection conn,
            @NonNull ExchangeDto dto) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(FIND_CROSS)) {
            stmt.setString(1, dto.value());
            stmt.setString(2, CROSS_CURRENCY_CODE);
            stmt.setString(3, dto.codes().base());
            stmt.setString(4, dto.codes().target());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return buildConvertedDto(rs);
            }

            throw new DaoNotFoundException("Currency not found");
        }
    }

    private ConvertedDto buildConvertedDto(ResultSet rs) throws SQLException {
        return ConvertedDto.of(
                currencyDao.buildCurrency(rs, "base_"),
                currencyDao.buildCurrency(rs, "target_"),
                rs.getBigDecimal("rate"),
                rs.getBigDecimal("amount"));
    }
}
