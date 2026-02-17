package org.betonquest.betonquest.quest.action.run;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.kernel.processor.adapter.ActionAdapter;
import org.betonquest.betonquest.kernel.registry.quest.ActionTypeRegistry;
import org.betonquest.betonquest.quest.action.eval.EvalAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory to allows for running multiple actions with one instruction string.
 */
public class RunActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The action type registry providing factories to parse the evaluated instruction.
     */
    private final ActionTypeRegistry actionTypeRegistry;

    /**
     * The {@link Instructions} to use.
     */
    private final Instructions instructions;

    /**
     * Create a run action factory with the given BetonQuest instance.
     *
     * @param instructions       the {@link Instructions} to use
     * @param actionTypeRegistry the action type registry providing factories to parse the evaluated instruction
     */
    public RunActionFactory(final Instructions instructions, final ActionTypeRegistry actionTypeRegistry) {
        this.instructions = instructions;
        this.actionTypeRegistry = actionTypeRegistry;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return createAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return createAction(instruction);
    }

    private NullableActionAdapter createAction(final Instruction instruction) throws QuestException {
        final List<String> parts = instruction.getValueParts();
        final List<ActionAdapter> actions = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (final String part : parts) {
            if (part.startsWith("^")) {
                if (!builder.isEmpty()) {
                    actions.add(EvalAction.createAction(instructions, actionTypeRegistry, instruction.getPackage(), builder.toString().trim()));
                    builder = new StringBuilder();
                }
                builder.append(part.substring(1)).append(' ');
            } else {
                builder.append(part).append(' ');
            }
        }
        if (!builder.isEmpty()) {
            actions.add(EvalAction.createAction(instructions, actionTypeRegistry, instruction.getPackage(), builder.toString().trim()));
        }
        return new NullableActionAdapter(new RunAction(actions));
    }
}
