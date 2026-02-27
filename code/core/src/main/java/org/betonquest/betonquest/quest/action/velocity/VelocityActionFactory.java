package org.betonquest.betonquest.quest.action.velocity;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.bukkit.util.Vector;

/**
 * Factory to create velocity actions from {@link Instruction}s.
 */
public class VelocityActionFactory implements PlayerActionFactory {

    /**
     * Create the velocity action factory.
     */
    public VelocityActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<Vector> vector = instruction.vector().get("vector").orElse(null);
        if (vector == null) {
            throw new QuestException("A 'vector' is required");
        }
        final Argument<VectorDirection> direction = instruction.enumeration(VectorDirection.class)
                .get("direction", VectorDirection.ABSOLUTE);
        final Argument<VectorModification> modification = instruction.enumeration(VectorModification.class)
                .get("modification", VectorModification.SET);
        return new OnlineActionAdapter(new VelocityAction(vector, direction, modification));
    }
}
