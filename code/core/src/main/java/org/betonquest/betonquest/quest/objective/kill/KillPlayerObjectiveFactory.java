package org.betonquest.betonquest.quest.objective.kill;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ConditionIdentifier;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Collections;
import java.util.List;

/**
 * Factory for creating {@link KillPlayerObjective} instances from {@link Instruction}s.
 */
public class KillPlayerObjectiveFactory implements ObjectiveFactory {

    /**
     * The condition manager to test conditions.
     */
    private final ConditionManager conditionManager;

    /**
     * Creates a new instance of the KillPlayerObjectiveFactory.
     *
     * @param conditionManager the condition manager to test conditions
     */
    public KillPlayerObjectiveFactory(final ConditionManager conditionManager) {
        this.conditionManager = conditionManager;
    }

    @Override
    public Objective parseInstruction(final Instruction instruction, final ObjectiveService service) throws QuestException {
        final Argument<Number> targetAmount = instruction.number().atLeast(1).get();
        final Argument<String> name = instruction.string().get("name").orElse(null);
        final Argument<List<ConditionIdentifier>> required = instruction.identifier(ConditionIdentifier.class)
                .list().get("required", Collections.emptyList());
        final KillPlayerObjective objective = new KillPlayerObjective(service, conditionManager, targetAmount, name, required);
        service.request(PlayerDeathEvent.class).onlineHandler(objective::onKill)
                .player(event -> event.getEntity().getKiller()).subscribe(true);
        return objective;
    }
}
