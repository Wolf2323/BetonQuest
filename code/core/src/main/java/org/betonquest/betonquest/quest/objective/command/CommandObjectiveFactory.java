package org.betonquest.betonquest.quest.objective.command;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ActionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.FlagArgument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Collections;
import java.util.List;

/**
 * Factory for creating {@link CommandObjective} instances from {@link Instruction}s.
 */
public class CommandObjectiveFactory implements ObjectiveFactory {

    /**
     * The action manager to run actions.
     */
    private final ActionManager actionManager;

    /**
     * Creates a new instance of the CommandObjectiveFactory.
     *
     * @param actionManager the action manager to run actions
     */
    public CommandObjectiveFactory(final ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<String> command = instruction.string().get();
        final FlagArgument<Boolean> ignoreCase = instruction.bool().getFlag("ignoreCase", true);
        final FlagArgument<Boolean> exact = instruction.bool().getFlag("exact", true);
        final FlagArgument<Boolean> cancel = instruction.bool().getFlag("cancel", true);
        final Argument<List<ActionIdentifier>> failEvents = instruction.identifier(ActionIdentifier.class)
                .list().get("failActions", Collections.emptyList());
        final CommandObjective objective = new CommandObjective(service, actionManager, command, ignoreCase, exact, cancel, failEvents);
        service.request(PlayerCommandPreprocessEvent.class).priority(EventPriority.LOWEST).onlineHandler(objective::onCommand)
                .player(PlayerCommandPreprocessEvent::getPlayer).subscribe(false);
        return objective;
    }
}
