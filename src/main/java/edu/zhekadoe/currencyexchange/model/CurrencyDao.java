package edu.zhekadoe.currencyexchange.model;

import edu.zhekadoe.currencyexchange.exception.DaoConflictException;
import edu.zhekadoe.currencyexchange.exception.DaoException;
import edu.zhekadoe.currencyexchange.exception.DaoNotFoundException;
import edu.zhekadoe.currencyexchange.utils.ConnectionManager;
import lombok.AccessLevel;
import org.sqlite.SQLiteException;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.sqlite.SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrencyDao {

    public static final String CURRENCY_CODE_ALREADY_EXISTS_MESSAGE = "A currency with this code already exists.";

    private static final CurrencyDao INSTANCE = new CurrencyDao();

    private static final String FIND_ALL_QUERY = """
            SELECT id, full_name, code, sign
            FROM currencies
            """;

    private static final String FIND_BY_CODE_QUERY = FIND_ALL_QUERY + """
            WHERE code = ?
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO currencies (full_name, code, sign)
            VALUES (?, ?, ?)
            RETURNING id, full_name, code, sign;
            """;

    public static CurrencyDao getInstance() {
        return INSTANCE;
    }

    public List<Currency> findAll() {
        try (var conn = ConnectionManager.get();
             var stmt = conn.prepareStatement(FIND_ALL_QUERY)) {
            ResultSet rs = stmt.executeQuery();
            List<Currency> result = new ArrayList<>();
            while (rs.next()) {
                result.add(buildCurrency(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public Currency findByCode(String code) {
        try (var conn = ConnectionManager.get()) {
            return findByCode(code, conn);
        } catch (SQLException e) {
            throw new DaoException(e.getMessage());
        }
    }

    public Currency findByCode(String code, Connection conn) throws SQLException {
        try (var stmt = conn.prepareStatement(FIND_BY_CODE_QUERY)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return buildCurrency(rs);
            }
            throw new DaoNotFoundException("Currency with code " + code + " not found");
        }
    }

    public Currency create(CurrencyDto dto) {
        try (Connection conn = ConnectionManager.get();
             var stmt = conn.prepareStatement(INSERT_QUERY)) {
            stmt.setString(1, dto.name());
            stmt.setString(2, dto.code());
            stmt.setString(3, dto.sign());
            ResultSet rs = stmt.executeQuery();
            rs.next();
            return buildCurrency(rs);
        } catch (SQLException e) {
            if (e instanceof SQLiteException sqliteE &&
                sqliteE.getResultCode() == SQLITE_CONSTRAINT_UNIQUE) {
                throw new DaoConflictException(CURRENCY_CODE_ALREADY_EXISTS_MESSAGE);
            }
            throw new DaoException(e.getMessage());
        }
    }

    public Currency buildCurrency(ResultSet rs, String prefix) throws SQLException {
        return Currency.of(
                rs.getLong(prefix + "id"),
                rs.getString(prefix + "full_name"),
                rs.getString(prefix + "code"),
                rs.getString(prefix + "sign"));
    }

    private Currency buildCurrency(ResultSet rs) throws SQLException {
        return buildCurrency(rs, "");
    }
}
