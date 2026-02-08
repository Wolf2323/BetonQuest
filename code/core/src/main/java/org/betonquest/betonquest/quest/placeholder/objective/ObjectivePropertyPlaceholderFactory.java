package org.betonquest.betonquest.quest.placeholder.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholderFactory;
import org.betonquest.betonquest.api.service.ObjectiveManager;

/**
 * Factory to create {@link ObjectivePropertyPlaceholder}s from {@link Instruction}s.
 * <p>
 * Format:
 * {@code %objective.<id>.<property>%}
 */
public class ObjectivePropertyPlaceholderFactory implements PlayerPlaceholderFactory {

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * Create a new factory to create Objective Property Placeholders.
     *
     * @param objectiveManager the objective manager
     */
    public ObjectivePropertyPlaceholderFactory(final ObjectiveManager objectiveManager) {
        this.objectiveManager = objectiveManager;
    }

    @Override
    public PlayerPlaceholder parsePlayer(final Instruction instruction) throws QuestException {
        final ObjectiveIdentifier objectiveID = instruction.identifier(ObjectiveIdentifier.class).get().getValue(null);
        return new ObjectivePropertyPlaceholder(objectiveManager, objectiveID, instruction.nextElement());
    }
}
