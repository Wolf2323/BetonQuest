package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.DefaultBetonQuestApi;
import org.betonquest.betonquest.api.service.action.Actions;
import org.betonquest.betonquest.api.service.condition.Conditions;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.item.Items;
import org.betonquest.betonquest.api.service.npc.Npcs;
import org.betonquest.betonquest.api.service.objective.Objectives;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link DefaultBetonQuestApi}.
 */
public class BetonQuestApiComponent extends AbstractCoreComponent {

    /**
     * The default BetonQuest API implementation to load.
     */
    @Nullable
    private DefaultBetonQuestApi defaultBetonQuestApi;

    /**
     * Create a new BetonQuestApiComponent.
     */
    public BetonQuestApiComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class, ServicesManager.class,
                QuestPackageManager.class, BetonQuestLoggerFactory.class, ProfileProvider.class,
                Identifiers.class, Instructions.class, Actions.class, Conditions.class, Objectives.class,
                Placeholders.class, Conversations.class, Items.class, Npcs.class);
    }

    @Override
    public boolean isLoaded() {
        return defaultBetonQuestApi != null;
    }

    @Override
    public void load(final DependencyProvider dependencyProvider) {
        final QuestPackageManager packManager = getDependency(QuestPackageManager.class);
        final ServicesManager servicesManager = getDependency(ServicesManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Actions actions = getDependency(Actions.class);
        final Conditions conditions = getDependency(Conditions.class);
        final Objectives objectives = getDependency(Objectives.class);
        final Placeholders placeholders = getDependency(Placeholders.class);
        final Conversations conversations = getDependency(Conversations.class);
        final Items items = getDependency(Items.class);
        final Npcs npcs = getDependency(Npcs.class);
        final Plugin plugin = getDependency(Plugin.class);

        this.defaultBetonQuestApi = new DefaultBetonQuestApi(profileProvider, packManager, loggerFactory, instructions,
                actions, conditions, objectives, placeholders, items, npcs,
                conversations, identifiers);

        servicesManager.register(BetonQuestApi.class, defaultBetonQuestApi, plugin, ServicePriority.Highest);

        dependencyProvider.take(DefaultBetonQuestApi.class, defaultBetonQuestApi);
    }
}
