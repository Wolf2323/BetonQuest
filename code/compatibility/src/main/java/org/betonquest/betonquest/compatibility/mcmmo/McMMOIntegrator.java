package org.betonquest.betonquest.compatibility.mcmmo;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.BetonQuestRegistries;
import org.betonquest.betonquest.compatibility.Integrator;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for McMMO.
 */
public class McMMOIntegrator implements Integrator {

    /**
     * Plugin to register listener with.
     */
    private final Plugin plugin;

    /**
     * Creates a new Integrator.
     *
     * @param plugin the plugin to register listener with
     */
    public McMMOIntegrator(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();

        final BetonQuestRegistries questRegistries = api.registries();
        questRegistries.conditions().register("mcmmolevel", new McMMOSkillLevelConditionFactory(loggerFactory));
        questRegistries.actions().register("mcmmoexp", new McMMOAddExpActionFactory(loggerFactory));
        final BetonQuestLogger log = api.loggerFactory().create(McMMOIntegrator.class);
        try {
            plugin.getServer().getPluginManager().registerEvents(new MCMMOQuestItemHandler(), plugin);
            log.debug("Enabled MCMMO QuestItemHandler");
        } catch (final LinkageError e) {
            log.warn("MCMMO version is not compatible with the QuestItemHandler.", e);
        }
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
