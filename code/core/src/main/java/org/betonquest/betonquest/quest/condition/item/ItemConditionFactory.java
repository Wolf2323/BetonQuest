package org.betonquest.betonquest.quest.condition.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.betonquest.betonquest.data.PlayerDataStorage;

import java.util.List;

/**
 * Factory for {@link ItemCondition}s.
 */
public class ItemConditionFactory implements PlayerConditionFactory {

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * Create the item factory.
     *
     * @param dataStorage the storage providing player data
     */
    public ItemConditionFactory(final PlayerDataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<List<ItemWrapper>> items = instruction.item().list().get();
        return new OnlineConditionAdapter(new ItemCondition(items, dataStorage));
    }
}
