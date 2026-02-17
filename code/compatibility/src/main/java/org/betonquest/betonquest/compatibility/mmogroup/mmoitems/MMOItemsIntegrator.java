package org.betonquest.betonquest.compatibility.mmogroup.mmoitems;

import net.Indyuce.mmoitems.MMOItems;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsApplyGemObjectiveFactory;
import org.betonquest.betonquest.compatibility.mmogroup.mmoitems.objective.MMOItemsUpgradeObjectiveFactory;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for MMO Items.
 */
public class MMOItemsIntegrator implements Integrator {

    /**
     * Plugin to register listener with.
     */
    private final Plugin plugin;

    /**
     * Creates a new Integrator.
     *
     * @param plugin the plugin to register listener with
     */
    public MMOItemsIntegrator(final Plugin plugin) {

        this.plugin = plugin;
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final ObjectiveRegistry objectiveRegistry = api.registries().objectives();
        objectiveRegistry.register("mmoitemupgrade", new MMOItemsUpgradeObjectiveFactory());
        objectiveRegistry.register("mmoitemapplygem", new MMOItemsApplyGemObjectiveFactory());

        final ItemRegistry itemRegistry = api.registries().items();
        itemRegistry.register("mmoitem", new MMOQuestItemFactory(MMOItems.plugin));
        itemRegistry.registerSerializer("mmoitem", new MMOQuestItemSerializer());
        plugin.getServer().getPluginManager().registerEvents(new MMOItemsCraftObjectiveAdder(api.profiles()), plugin);
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
