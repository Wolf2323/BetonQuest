package org.betonquest.betonquest.quest.condition.armor;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link ArmorCondition}s from {@link Instruction}s.
 */
public class ArmorConditionFactory implements PlayerConditionFactory {

    /**
     * Create the armor factory.
     */
    public ArmorConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ItemWrapper> armorItem = instruction.item().get();
        return new OnlineConditionAdapter(new ArmorCondition(armorItem));
    }
}
