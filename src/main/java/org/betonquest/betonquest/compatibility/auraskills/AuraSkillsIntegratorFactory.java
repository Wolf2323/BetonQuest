package org.betonquest.betonquest.compatibility.auraskills;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.bukkit.plugin.Plugin;

/**
 * Factory for creating {@link AuraSkillsIntegrator} instances.
 */
public class AuraSkillsIntegratorFactory implements IntegratorFactory {
    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Creates a new instance of the factory.
     *
     * @param plugin the plugin instance
     */
    public AuraSkillsIntegratorFactory(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Integrator getIntegrator() {
        return new AuraSkillsIntegrator(plugin);
    }
}
