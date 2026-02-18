package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.BetonQuestApiService;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.action.Actions;
import org.betonquest.betonquest.api.service.condition.Conditions;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.item.Items;
import org.betonquest.betonquest.api.service.npc.Npcs;
import org.betonquest.betonquest.api.service.objective.Objectives;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;

import java.util.function.Supplier;

/**
 * The default implementation of the {@link BetonQuestApiService}.
 */
public class DefaultBetonQuestApi implements BetonQuestApi {

    /**
     * The {@link ProfileProvider} supplier.
     */
    private final Supplier<ProfileProvider> profileProviderSupplier;

    /**
     * The {@link QuestPackageManager} supplier.
     */
    private final Supplier<QuestPackageManager> questPackageManagerSupplier;

    /**
     * The {@link BetonQuestLoggerFactory} supplier.
     */
    private final Supplier<BetonQuestLoggerFactory> loggerFactorySupplier;

    /**
     * The {@link Instructions} supplier.
     */
    private final Supplier<Instructions> instructionsSupplier;

    /**
     * The {@link Conversations} supplier.
     */
    private final Supplier<Conversations> conversationsSupplier;

    /**
     * Creates a new instance of the {@link DefaultBetonQuestApi}.
     *
     * @param profileProviderSupplier the {@link ProfileProvider} supplier
     * @param packageManagerSupplier  the {@link QuestPackageManager} supplier
     * @param loggerFactorySupplier   the {@link BetonQuestLoggerFactory} supplier
     * @param instructionsSupplier    the {@link Instructions} supplier
     * @param conversationsSupplier   the {@link Conversations} supplier
     */
    public DefaultBetonQuestApi(final Supplier<ProfileProvider> profileProviderSupplier, final Supplier<QuestPackageManager> packageManagerSupplier,
                                final Supplier<BetonQuestLoggerFactory> loggerFactorySupplier, final Supplier<Instructions> instructionsSupplier,
                                final Supplier<Conversations> conversationsSupplier) {
        this.profileProviderSupplier = profileProviderSupplier;
        this.questPackageManagerSupplier = packageManagerSupplier;
        this.loggerFactorySupplier = loggerFactorySupplier;
        this.instructionsSupplier = instructionsSupplier;
        this.conversationsSupplier = conversationsSupplier;
    }

    @Override
    public ProfileProvider profiles() {
        return profileProviderSupplier.get();
    }

    @Override
    public QuestPackageManager packages() {
        return questPackageManagerSupplier.get();
    }

    @Override
    public BetonQuestLoggerFactory loggerFactory() {
        return loggerFactorySupplier.get();
    }

    @Override
    public Instructions instructions() {
        return instructionsSupplier.get();
    }

    @Override
    public Conversations conversations() {
        return conversationsSupplier.get();
    }

    @Override
    public Identifiers identifiers() {
        return null;
    }

    @Override
    public Actions actions() {
        return null;
    }

    @Override
    public Conditions conditions() {
        return null;
    }

    @Override
    public Objectives objectives() {
        return null;
    }

    @Override
    public Placeholders placeholders() {
        return null;
    }

    @Override
    public Items items() {
        return null;
    }

    @Override
    public Npcs npcs() {
        return null;
    }
}
