package org.betonquest.betonquest.quest.action.eval;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.NullableActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.api.quest.action.PlayerlessAction;
import org.betonquest.betonquest.api.quest.action.PlayerlessActionFactory;
import org.betonquest.betonquest.api.service.Instructions;
import org.betonquest.betonquest.kernel.registry.quest.ActionTypeRegistry;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * A factory for creating Eval actions.
 */
public class EvalActionFactory implements PlayerActionFactory, PlayerlessActionFactory {

    /**
     * The BetonQuest instructions to use to parse instructions.
     */
    private final Instructions instructions;

    /**
     * The action type registry providing factories to parse the evaluated instruction.
     */
    private final ActionTypeRegistry actionTypeRegistry;

    /**
     * The scheduler to use for synchronous execution.
     */
    private final BukkitScheduler scheduler;

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * Create a new Eval action factory.
     *
     * @param instructions       the BetonQuest instructions to use to parse instructions
     * @param actionTypeRegistry the action type registry providing factories to parse the evaluated instruction
     * @param scheduler          the scheduler to use for synchronous execution
     * @param plugin             the plugin instance
     */
    public EvalActionFactory(final Instructions instructions, final ActionTypeRegistry actionTypeRegistry, final BukkitScheduler scheduler, final Plugin plugin) {
        this.instructions = instructions;
        this.actionTypeRegistry = actionTypeRegistry;
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        return parseEvalAction(instruction);
    }

    @Override
    public PlayerlessAction parsePlayerless(final Instruction instruction) throws QuestException {
        return parseEvalAction(instruction);
    }

    private NullableActionAdapter parseEvalAction(final Instruction instruction) throws QuestException {
        final String rawInstruction = String.join(" ", instruction.getValueParts());
        return new NullableActionAdapter(new EvalAction(instructions, actionTypeRegistry, instruction.getPackage(),
                instruction.chainForArgument(rawInstruction).string().get(), scheduler, plugin));
    }
}
