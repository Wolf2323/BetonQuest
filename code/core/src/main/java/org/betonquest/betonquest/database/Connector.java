package org.betonquest.betonquest.database;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * Connects to the database and queries it.
 */
public class Connector {

    /**
     * Table prefix.
     */
    private final String prefix;

    /**
     * Database connection management.
     */
    private final Database database;

    /**
     * Opens a new connection to the database.
     *
     * @param prefix   the database table prefix
     * @param database the database to connect to
     */
    public Connector(final String prefix, final Database database) {
        this.prefix = prefix;
        this.database = database;
    }

    /**
     * Queries the database with the given type and arguments.
     *
     * @param type type of the query
     * @param args arguments
     * @return ResultSet with the requested data
     */
    @SuppressWarnings("PMD.CloseResource")
    @SuppressFBWarnings("ODR_OPEN_DATABASE_RESOURCE")
    public ResultSet querySQL(final QueryType type, final Object... args) {
        final String sql = type.createSql(prefix);
        try {
            final PreparedStatement statement = database.getConnection().prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            return statement.executeQuery();
        } catch (final SQLException e) {
            throw new IllegalStateException(
                    "There was a exception with SQL executing query type '%s' with the following arguments: %s. Reason: %s"
                            .formatted(type, Arrays.toString(args), e.getMessage()), e);
        }
    }

    /**
     * Updates the database with the given type and arguments.
     *
     * @param type type of the update
     * @param args arguments
     */
    public void updateSQL(final UpdateType type, final Object... args) {
        final String sql = type.createSql(prefix);
        try (PreparedStatement statement = database.getConnection().prepareStatement(sql)) {
            for (int i = 0; i < args.length; i++) {
                statement.setObject(i + 1, args[i]);
            }
            statement.executeUpdate();
        } catch (final SQLException e) {
            throw new IllegalStateException(
                    "There was an exception with SQL executing update type '%s' with the following arguments: %s. Reason: %s"
                            .formatted(type, Arrays.toString(args), e.getMessage()), e);
        }
    }

    /**
     * Gets the database.
     *
     * @return the database used for connections
     */
    public Database getDatabase() {
        return database;
    }
}
