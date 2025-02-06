package org.betonquest.betonquest.quest.event.compass;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.config.Config;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.quest.event.PrimaryServerThreadEvent;
import org.betonquest.betonquest.util.Utils;
import org.betonquest.betonquest.variables.GlobalVariableResolver;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;

/**
 * The compass event factory.
 */
public class CompassEventFactory implements EventFactory {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Storage to get the offline player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Plugin manager to use to call the event.
     */
    private final PluginManager pluginManager;

    /**
     * Data for primary server thread access.
     */
    private final PrimaryServerThreadData data;

    /**
     * Create the compass event factory.
     *
     * @param loggerFactory logger factory to use
     * @param dataStorage   the storage for used player data
     * @param pluginManager plugin manager to use
     * @param data          the data for primary server thread access
     */
    public CompassEventFactory(final BetonQuestLoggerFactory loggerFactory, final PlayerDataStorage dataStorage,
                               final PluginManager pluginManager, final PrimaryServerThreadData data) {
        this.log = loggerFactory.create(CompassEvent.class);
        this.dataStorage = dataStorage;
        this.pluginManager = pluginManager;
        this.data = data;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        final CompassTargetAction action = instruction.getEnum(CompassTargetAction.class);
        final String compass = Utils.addPackage(instruction.getPackage(), instruction.next());
        final VariableLocation compassLocation = getCompassLocation(compass);
        return new PrimaryServerThreadEvent(
                new CompassEvent(log, dataStorage, pluginManager, action, compass, compassLocation, instruction.getPackage()),
                data);
    }

    private VariableLocation getCompassLocation(final String compass) throws QuestException {
        final String[] split = compass.split("\\.", 2);
        final QuestPackage targetPack = Config.getPackages().get(split[0]);
        if (targetPack == null) {
            throw new QuestException("QuestPackage '" + split[0] + "' not found");
        }
        final ConfigurationSection section = targetPack.getConfig().getConfigurationSection("compass." + split[1]);
        if (section == null) {
            throw new QuestException("The compass '" + compass + "' does not exist");
        }
        final String location = GlobalVariableResolver.resolve(targetPack, section.getString("location"));
        return new VariableLocation(BetonQuest.getInstance().getVariableProcessor(), targetPack,
                Utils.getNN(location, "Missing location in compass section"));
    }
}
