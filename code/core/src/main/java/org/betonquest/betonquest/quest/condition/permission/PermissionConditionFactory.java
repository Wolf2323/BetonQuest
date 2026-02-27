package org.betonquest.betonquest.quest.condition.permission;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory for {@link PermissionCondition}s.
 */
public class PermissionConditionFactory implements PlayerConditionFactory {

    /**
     * Creates a new factory for {@link PermissionCondition}s.
     */
    public PermissionConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> permission = instruction.string().get();
        return new OnlineConditionAdapter(new PermissionCondition(permission));
    }
}
