package org.betonquest.betonquest.quest.action.item;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.quest.action.point.PointType;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Random;

/**
 * Factory for the item durability action.
 */
public class ItemDurabilityActionFactory implements PlayerActionFactory {

    /**
     * Create the item durability action factory.
     */
    public ItemDurabilityActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<EquipmentSlot> slot = instruction.enumeration(EquipmentSlot.class).get();
        final Argument<PointType> operation = instruction.enumeration(PointType.class).get();
        final Argument<Number> amount = instruction.number().get();
        final FlagArgument<Boolean> ignoreUnbreakable = instruction.bool().getFlag("ignoreUnbreakable", true);
        final FlagArgument<Boolean> ignoreEvents = instruction.bool().getFlag("ignoreEvents", true);
        return new OnlineActionAdapter(new ItemDurabilityAction(slot, operation, amount, ignoreUnbreakable, ignoreEvents, new Random()));
    }
}
