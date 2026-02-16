package org.betonquest.betonquest.compatibility.heroes;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.CharacterManager;
import com.herocraftonline.heroes.characters.classes.HeroClassManager;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.BetonQuestRegistries;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.heroes.action.HeroesExperienceActionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesAttributeConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesClassConditionFactory;
import org.betonquest.betonquest.compatibility.heroes.condition.HeroesSkillConditionFactory;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for Heroes.
 */
public class HeroesIntegrator implements Integrator {

    /**
     * Plugin to register listener with.
     */
    private final Plugin plugin;

    /**
     * Creates a new Integrator.
     *
     * @param plugin the plugin to register listener with
     */
    public HeroesIntegrator(final Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        final CharacterManager characterManager = Heroes.getInstance().getCharacterManager();
        final HeroClassManager classManager = Heroes.getInstance().getClassManager();

        final BetonQuestRegistries questRegistries = api.registries();
        final ConditionRegistry conditionRegistry = questRegistries.conditions();
        conditionRegistry.register("heroesattribute", new HeroesAttributeConditionFactory(loggerFactory, characterManager));
        conditionRegistry.register("heroesclass", new HeroesClassConditionFactory(loggerFactory, characterManager, classManager));
        conditionRegistry.register("heroesskill", new HeroesSkillConditionFactory(loggerFactory, characterManager));

        questRegistries.actions().register("heroesexp", new HeroesExperienceActionFactory(loggerFactory, characterManager));

        plugin.getServer().getPluginManager().registerEvents(new HeroesMobKillListener(api.profiles()), plugin);
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
