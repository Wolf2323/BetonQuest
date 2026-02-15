package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.BetonQuestApiService;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;

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
     * The {@link BetonQuestInstructions} supplier.
     */
    private final Supplier<BetonQuestInstructions> instructionsSupplier;

    /**
     * The {@link BetonQuestConversations} supplier.
     */
    private final Supplier<BetonQuestConversations> conversationsSupplier;

    /**
     * The {@link BetonQuestRegistries} supplier.
     */
    private final Supplier<BetonQuestRegistries> registriesSupplier;

    /**
     * The {@link BetonQuestManagers} supplier.
     */
    private final Supplier<BetonQuestManagers> managersSupplier;

    /**
     * Creates a new instance of the {@link DefaultBetonQuestApi}.
     *
     * @param profileProviderSupplier the {@link ProfileProvider} supplier
     * @param packageManagerSupplier  the {@link QuestPackageManager} supplier
     * @param loggerFactorySupplier   the {@link BetonQuestLoggerFactory} supplier
     * @param instructionsSupplier    the {@link BetonQuestInstructions} supplier
     * @param conversationsSupplier   the {@link BetonQuestConversations} supplier
     * @param registriesSupplier      the {@link BetonQuestRegistries} supplier
     * @param managersSupplier        the {@link BetonQuestManagers} supplier
     */
    public DefaultBetonQuestApi(final Supplier<ProfileProvider> profileProviderSupplier, final Supplier<QuestPackageManager> packageManagerSupplier,
                                final Supplier<BetonQuestLoggerFactory> loggerFactorySupplier, final Supplier<BetonQuestInstructions> instructionsSupplier,
                                final Supplier<BetonQuestConversations> conversationsSupplier, final Supplier<BetonQuestRegistries> registriesSupplier,
                                final Supplier<BetonQuestManagers> managersSupplier) {
        this.profileProviderSupplier = profileProviderSupplier;
        this.questPackageManagerSupplier = packageManagerSupplier;
        this.loggerFactorySupplier = loggerFactorySupplier;
        this.instructionsSupplier = instructionsSupplier;
        this.conversationsSupplier = conversationsSupplier;
        this.registriesSupplier = registriesSupplier;
        this.managersSupplier = managersSupplier;
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
    public BetonQuestLoggerFactory loggers() {
        return loggerFactorySupplier.get();
    }

    @Override
    public BetonQuestInstructions instructions() {
        return instructionsSupplier.get();
    }

    @Override
    public BetonQuestConversations conversations() {
        return conversationsSupplier.get();
    }

    @Override
    public BetonQuestRegistries registries() {
        return registriesSupplier.get();
    }

    @Override
    public BetonQuestManagers managers() {
        return managersSupplier.get();
    }
}
