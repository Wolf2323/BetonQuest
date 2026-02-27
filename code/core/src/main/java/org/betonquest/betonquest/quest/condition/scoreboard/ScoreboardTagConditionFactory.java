package org.betonquest.betonquest.quest.condition.scoreboard;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create scoreboard tag conditions from {@link Instruction}s.
 */
public class ScoreboardTagConditionFactory implements PlayerConditionFactory {

    /**
     * Create the scoreboard tag condition factory.
     */
    public ScoreboardTagConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> tag = instruction.string().get();
        return new OnlineConditionAdapter(new ScoreboardTagCondition(tag));
    }
}
