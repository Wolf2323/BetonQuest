package org.betonquest.betonquest.feature.registry.processor;

import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.message.MessageParser;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.id.CompassID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.message.ParsedSectionMessage;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Loads and stores {@link QuestCompass}es.
 */
public class CompassProcessor extends SectionProcessor<CompassID, QuestCompass> {

    /**
     * Variable processor to create new variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * The message parser to parse the message.
     */
    private final MessageParser messageParser;

    /**
     * The player data storage to get the player's language.
     */
    private final PlayerDataStorage playerDataStorage;

    /**
     * Create a new QuestProcessor to store {@link QuestCompass}es.
     *
     * @param log               the custom logger for this class
     * @param variableProcessor the variable processor to create new variables
     * @param messageParser     the message parser to parse the message
     * @param playerDataStorage the player data storage to get the player's language
     */
    public CompassProcessor(final BetonQuestLogger log, final VariableProcessor variableProcessor,
                            final MessageParser messageParser, final PlayerDataStorage playerDataStorage) {
        super(log, "Compass", "compass");
        this.variableProcessor = variableProcessor;
        this.messageParser = messageParser;
        this.playerDataStorage = playerDataStorage;
    }

    @Override
    protected QuestCompass loadSection(final QuestPackage pack, final ConfigurationSection section) throws QuestException {
        final ParsedSectionMessage names = new ParsedSectionMessage(variableProcessor, messageParser, playerDataStorage, pack, section, "name");
        final String location = section.getString("location");
        if (location == null) {
            throw new QuestException("Location not defined");
        }
        final VariableLocation loc = new VariableLocation(variableProcessor, pack, GlobalVariableResolver.resolve(pack, location));
        final String itemName = section.getString("item");
        final ItemID itemID = itemName == null ? null : new ItemID(pack, itemName);
        return new QuestCompass(names, loc, itemID);
    }

    @Override
    protected CompassID getIdentifier(final QuestPackage pack, final String identifier) throws QuestException {
        return new CompassID(pack, identifier);
    }
}
