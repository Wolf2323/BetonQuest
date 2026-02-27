package org.betonquest.betonquest.quest.condition.journal;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

/**
 * Factory for {@link JournalCondition}s.
 */
public class JournalConditionFactory implements PlayerConditionFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the journal condition factory.
     *
     * @param dataStorage the storage providing player data
     */
    public JournalConditionFactory(final PlayerDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<JournalEntryIdentifier> entryID = instruction.identifier(JournalEntryIdentifier.class).get();
        return new OnlineConditionAdapter(new JournalCondition(dataStorage, entryID));
    }
}
