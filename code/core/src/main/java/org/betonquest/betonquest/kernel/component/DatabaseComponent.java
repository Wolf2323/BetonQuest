package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.database.Connector;
import org.betonquest.betonquest.database.Database;
import org.betonquest.betonquest.database.MySQL;
import org.betonquest.betonquest.database.SQLite;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.bukkit.plugin.Plugin;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Connector}.
 */
public class DatabaseComponent extends AbstractCoreComponent {

    /**
     * Whether the database is using MySQL.
     */
    private boolean usesMySQL;

    /**
     * Create a new DatabaseComponent.
     */
    public DatabaseComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, BetonQuestLoggerFactory.class, ConfigAccessor.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final Plugin plugin = getDependency(Plugin.class);

        final BetonQuestLogger log = loggerFactory.create(DatabaseComponent.class);

        final boolean mySQLEnabled = config.getBoolean("mysql.enabled", true);
        Database database = null;
        if (mySQLEnabled) {
            log.debug("Connecting to MySQL database");
            final Database mySql = new MySQL(loggerFactory.create(MySQL.class, "Database"), plugin, config,
                    config.getString("mysql.host"),
                    config.getString("mysql.port"),
                    config.getString("mysql.base"),
                    config.getString("mysql.user"),
                    config.getString("mysql.pass"));
            try {
                mySql.getConnection();
                database = mySql;
                usesMySQL = true;
                log.info("Successfully connected to MySQL database!");
            } catch (final IllegalStateException e) {
                log.warn("MySQL: " + e.getMessage(), e);
            }
        }
        if (database == null) {
            database = new SQLite(loggerFactory.create(SQLite.class, "Database"), plugin, config, "database.db");
            if (mySQLEnabled) {
                log.warn("No connection to the mySQL Database! Using SQLite for storing data as fallback!");
            } else {
                log.info("Using SQLite for storing data!");
            }
        }

        database.createTables();
        final Connector connector = new Connector(loggerFactory.create(Connector.class), config.getString("mysql.prefix"), database);

        dependencyProvider.take(Connector.class, connector);
    }

    /**
     * Whether the database is using MySQL.
     *
     * @return if the database is using MySQL
     */
    public boolean usesMySQL() {
        return usesMySQL;
    }
}
