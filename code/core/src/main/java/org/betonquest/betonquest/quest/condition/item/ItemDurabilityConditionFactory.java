package org.betonquest.betonquest.quest.condition.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Factory for {@link ItemDurabilityCondition}s.
 */
public class ItemDurabilityConditionFactory implements PlayerConditionFactory {

    /**
     * Create the item durability factory.
     */
    public ItemDurabilityConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<EquipmentSlot> slot = instruction.enumeration(EquipmentSlot.class).get();
        final Argument<Number> amount = instruction.number().get();
        final FlagArgument<Boolean> relative = instruction.bool().getFlag("relative", true);
        return new OnlineConditionAdapter(new ItemDurabilityCondition(slot, amount, relative));
    }
}
