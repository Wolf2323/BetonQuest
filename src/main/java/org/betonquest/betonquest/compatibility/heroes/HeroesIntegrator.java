package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.classes.HeroClassManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesAttributeConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesClassConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesSkillConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.event.HeroesExperienceEventFactory;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for Heroes.
 */
public class HeroesIntegrator implements Integrator {
    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The logger factory used by BetonQuest.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The server instance.
     */
    private final Server server;

    /**
     * The default constructor.
     *
     * @param plugin          the plugin instance
     * @param loggerFactory   the logger factory used by BetonQuest
     * @param profileProvider the profile provider instance
     * @param server          the server instance
     */
    public HeroesIntegrator(final Plugin plugin, final BetonQuestLoggerFactory loggerFactory, final ProfileProvider profileProvider, final Server server) {
        this.plugin = plugin;
        this.loggerFactory = loggerFactory;
        this.profileProvider = profileProvider;
        this.server = server;
    }

    @Override
    public void hook(final QuestTypeRegistries questTypeRegistries, final FeatureRegistries featureRegistries) {
        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        final CharacterManager characterManager = Heroes.getInstance().getCharacterManager();
        final HeroClassManager classManager = Heroes.getInstance().getClassManager();

        final ConditionTypeRegistry conditionTypes = questTypeRegistries.condition();
        conditionTypes.register("heroesattribute", new HeroesAttributeConditionFactory(loggerFactory, data, characterManager));
        conditionTypes.register("heroesclass", new HeroesClassConditionFactory(loggerFactory, data, characterManager, classManager));
        conditionTypes.register("heroesskill", new HeroesSkillConditionFactory(loggerFactory, data, characterManager));

        questTypeRegistries.event().register("heroesexp", new HeroesExperienceEventFactory(loggerFactory, data, characterManager));

        plugin.getServer().getPluginManager().registerEvents(new HeroesMobKillListener(profileProvider), plugin);
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
