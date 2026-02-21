package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.identifier.NpcIdentifier;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.api.service.npc.NpcRegistry;
import org.betonquest.betonquest.api.service.npc.Npcs;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.id.conversation.ConversationIdentifierFactory;
import org.betonquest.betonquest.id.npc.NpcIdentifierFactory;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.processor.feature.ConversationProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ActionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.ConditionProcessor;
import org.betonquest.betonquest.kernel.processor.quest.NpcProcessor;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link Npcs}.
 */
public class NpcsComponent extends AbstractCoreComponent implements Npcs {

    /**
     * The NPC type registry to load.
     */
    @Nullable
    private NpcTypeRegistry npcTypeRegistry;

    /**
     * The NPC processor to load.
     */
    @Nullable
    private NpcProcessor npcProcessor;

    /**
     * Create a new NpcsComponent.
     */
    public NpcsComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(Plugin.class,
                QuestPackageManager.class, BetonQuestLoggerFactory.class, ProfileProvider.class, ConfigAccessor.class,
                Saver.class, PluginMessage.class, Instructions.class, Identifiers.class,
                ConversationProcessor.class, ActionProcessor.class, ConditionProcessor.class,
                ConversationIdentifierFactory.class);
    }

    @Override
    public boolean isLoaded() {
        return npcTypeRegistry != null && npcProcessor != null;
    }

    @Override
    public void load(final DependencyProvider providerCallback) {
        final QuestPackageManager questPackageManager = getDependency(QuestPackageManager.class);
        final BetonQuestLoggerFactory loggerFactory = getDependency(BetonQuestLoggerFactory.class);
        final Instructions instructions = getDependency(Instructions.class);
        final ProfileProvider profileProvider = getDependency(ProfileProvider.class);
        final ConversationProcessor conversationProcessor = getDependency(ConversationProcessor.class);
        final ActionProcessor actionProcessor = getDependency(ActionProcessor.class);
        final ConditionProcessor conditionProcessor = getDependency(ConditionProcessor.class);
        final ConversationIdentifierFactory conversationIdentifierFactory = getDependency(ConversationIdentifierFactory.class);
        final PluginMessage pluginMessage = getDependency(PluginMessage.class);
        final ConfigAccessor config = getDependency(ConfigAccessor.class);
        final Saver saver = getDependency(Saver.class);
        final Identifiers identifiers = getDependency(Identifiers.class);
        final Plugin plugin = getDependency(Plugin.class);

        final NpcIdentifierFactory npcIdentifierFactory = new NpcIdentifierFactory(questPackageManager);
        identifiers.register(NpcIdentifier.class, npcIdentifierFactory);
        this.npcTypeRegistry = new NpcTypeRegistry(loggerFactory.create(NpcTypeRegistry.class), instructions);
        this.npcProcessor = new NpcProcessor(loggerFactory.create(NpcProcessor.class), loggerFactory, plugin,
                npcIdentifierFactory, conversationIdentifierFactory, npcTypeRegistry, pluginMessage,
                profileProvider, actionProcessor, conditionProcessor, conversationProcessor.getStarter(), instructions,
                identifiers, saver, config, conversationProcessor);

        providerCallback.take(NpcIdentifierFactory.class, npcIdentifierFactory);
        providerCallback.take(NpcTypeRegistry.class, npcTypeRegistry);
        providerCallback.take(NpcProcessor.class, npcProcessor);
        providerCallback.take(Npcs.class, this);
    }

    @Override
    public NpcManager manager() {
        return Objects.requireNonNull(npcProcessor, "NPC processor not loaded yet");
    }

    @Override
    public NpcRegistry registry() {
        return Objects.requireNonNull(npcTypeRegistry, "NPC registry not loaded yet");
    }
}
