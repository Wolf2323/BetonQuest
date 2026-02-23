package org.betonquest.betonquest.quest.action.command;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.bukkit.Server;

/**
 * Creates new {@link SudoAction}s from {@link Instruction}s.
 */
public class SudoActionFactory extends BaseCommandActionFactory {

    /**
     * Create the sudo action factory.
     *
     * @param server the server to execute commands on
     */
    public SudoActionFactory(final Server server) {
        super(server);
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return new OnlineActionAdapter(new SudoAction(parseCommands(instruction)));
    }
}
