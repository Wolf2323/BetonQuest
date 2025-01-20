package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.database.UpdateType;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;

/**
 * Deletes the points in the category from all online players and database entries.
 */
public class DeletePointStaticEvent implements StaticEvent {
    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Database saver to use for writing offline player data.
     */
    private final Saver saver;

    /**
     * Point category to remove.
     */
    private final VariableIdentifier category;

    /**
     * Create a new Point remove event for every player, online and offline.
     *
     * @param dataStorage the storage providing player data
     * @param saver       the saver to use
     * @param category    the category to remove
     */
    public DeletePointStaticEvent(final PlayerDataStorage dataStorage, final Saver saver, final VariableIdentifier category) {
        this.dataStorage = dataStorage;
        this.saver = saver;
        this.category = category;
    }

    @Override
    public void execute() throws QuestException {
        final String category = this.category.getValue(null);
        for (final OnlineProfile onlineProfile : BetonQuest.getInstance().getProfileProvider().getOnlineProfiles()) {
            dataStorage.get(onlineProfile).removePointsCategory(category);
        }
        saver.add(new Saver.Record(UpdateType.REMOVE_ALL_POINTS, category));
    }
}
