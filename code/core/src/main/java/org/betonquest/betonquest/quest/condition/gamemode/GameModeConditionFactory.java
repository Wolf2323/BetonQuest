package org.betonquest.betonquest.quest.condition.gamemode;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;
import org.bukkit.GameMode;

/**
 * Factory for {@link GameModeCondition}s.
 */
public class GameModeConditionFactory implements PlayerConditionFactory {

    /**
     * Create the game mode factory.
     */
    public GameModeConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<GameMode> gameMode = instruction.enumeration(GameMode.class).get();
        return new OnlineConditionAdapter(new GameModeCondition(gameMode));
    }
}
