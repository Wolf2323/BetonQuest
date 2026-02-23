package org.betonquest.betonquest.quest.action.scoreboard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;

/**
 * Factory to create scoreboard tag actions from {@link Instruction}s.
 */
public class ScoreboardTagActionFactory implements PlayerActionFactory {

    /**
     * Create the scoreboard tag action factory.
     */
    public ScoreboardTagActionFactory() {
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<ScoreboardTagOperation> action = instruction.enumeration(ScoreboardTagOperation.class).get();
        final Argument<String> tag = instruction.string().get();
        return new OnlineActionAdapter(new ScoreboardTagAction(tag, action));
    }
}
