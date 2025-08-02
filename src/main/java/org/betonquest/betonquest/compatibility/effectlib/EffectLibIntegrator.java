package org.betonquest.betonquest.compatibility.effectlib;

import de.slikey.effectlib.EffectManager;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.effectlib.event.ParticleEventFactory;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Integrator for <a href="https://github.com/elBukkit/EffectLib/">EffectLib</a>.
 */
public class EffectLibIntegrator implements Integrator {
    /**
     * The BetonQuest instance.
     */
    private final BetonQuest betonQuest;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The logger factory used by BetonQuest.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Effect manager starting and controlling effects.
     */
    @Nullable
    private EffectManager manager;

    /**
     * Particle Manager displaying effects on NPCs.
     */
    @Nullable
    private EffectLibParticleManager particleManager;

    /**
     * The default Constructor.
     *
     * @param betonQuest    the BetonQuest instance
     * @param plugin        the plugin instance
     * @param loggerFactory the logger factory used by BetonQuest
     */
    public EffectLibIntegrator(final BetonQuest betonQuest, final Plugin plugin, final BetonQuestLoggerFactory loggerFactory) {
        this.betonQuest = betonQuest;
        this.plugin = plugin;
        this.loggerFactory = loggerFactory;
    }

    @Override
    public void hook(final QuestTypeRegistries questTypeRegistries, final FeatureRegistries featureRegistries) {
        manager = new EffectManager(plugin);
        final PrimaryServerThreadData data = new PrimaryServerThreadData(plugin.getServer(), plugin.getServer().getScheduler(), plugin);
        questTypeRegistries.event().register("particle", new ParticleEventFactory(loggerFactory, data, manager));
    }

    @Override
    public void postHook() {
        if (manager != null) {
            particleManager = new EffectLibParticleManager(betonQuest, plugin, loggerFactory.create(EffectLibParticleManager.class),
                    loggerFactory, betonQuest.getQuestTypeAPI(), betonQuest.getFeatureAPI(), betonQuest.getProfileProvider(),
                    betonQuest.getVariableProcessor(), manager);
        }
    }

    @Override
    public void reload() {
        if (particleManager != null) {
            particleManager.reload();
        }
    }

    @Override
    public void close() {
        if (manager != null) {
            manager.dispose();
        }
    }
}
