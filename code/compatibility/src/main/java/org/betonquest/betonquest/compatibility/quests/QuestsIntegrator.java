package org.betonquest.betonquest.compatibility.quests;

import me.pikamug.quests.Quests;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.identifier.IdentifierFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.BetonQuestRegistries;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

/**
 * Integrator for the Quests plugin.
 */
public class QuestsIntegrator implements Integrator {

    /**
     * The default constructor.
     */
    public QuestsIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) throws HookException {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin("Quests");
        final Quests questsInstance = (Quests) plugin;
        Objects.requireNonNull(questsInstance);

        final BetonQuestRegistries questRegistries = api.registries();
        api.conditions().registry().register("quest", new QuestsConditionFactory(questsInstance));
        api.actions().registry().register("quest", new QuestsActionFactory(questsInstance));

        final BetonQuestLoggerFactory loggerFactory = api.loggerFactory();
        final ProfileProvider profileProvider = api.profiles();
        try {
            final IdentifierFactory<ActionIdentifier> actionIdentifierFactory =
                    questRegistries.identifiers().getFactory(ActionIdentifier.class);
            questsInstance.getCustomRewards().add(new ActionReward(
                    loggerFactory.create(ActionReward.class),
                    api.actions().manager(), profileProvider, actionIdentifierFactory));
        } catch (final QuestException e) {
            throw new HookException(plugin, "Could not add custom action reward while hooking into Quests.", e);
        }
        try {
            final IdentifierFactory<ConditionIdentifier> conditionIdentifierFactory =
                    questRegistries.identifiers().getFactory(ConditionIdentifier.class);
            questsInstance.getCustomRequirements().add(new ConditionRequirement(
                    loggerFactory.create(ConditionRequirement.class),
                    api.conditions().manager(), profileProvider, conditionIdentifierFactory));
        } catch (final QuestException e) {
            throw new HookException(plugin, "Could not add custom condition requirement while hooking into Quests.", e);
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
