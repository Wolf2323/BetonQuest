package org.betonquest.betonquest.quest.event.point;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.Event;
import org.betonquest.betonquest.api.quest.event.EventFactory;
import org.betonquest.betonquest.api.quest.event.StaticEvent;
import org.betonquest.betonquest.api.quest.event.StaticEventFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableIdentifier;

/**
 * Factory to create delete points events from {@link Instruction}s.
 */
public class DeletePointEventFactory implements EventFactory, StaticEventFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Database saver to use for writing offline player data.
     */
    private final Saver saver;

    /**
     * Create the delete points event factory.
     *
     * @param dataStorage the storage providing player data
     * @param saver       the saver to use
     */
    public DeletePointEventFactory(final PlayerDataStorage dataStorage, final Saver saver) {
        this.dataStorage = dataStorage;
        this.saver = saver;
    }

    @Override
    public Event parseEvent(final Instruction instruction) throws QuestException {
        return new DeletePointEvent(dataStorage::getOffline, instruction.get(VariableIdentifier::new));
    }

    @Override
    public StaticEvent parseStaticEvent(final Instruction instruction) throws QuestException {
        final VariableIdentifier category = instruction.get(VariableIdentifier::new);
        return new DeletePointStaticEvent(dataStorage, saver, category);
    }
}
