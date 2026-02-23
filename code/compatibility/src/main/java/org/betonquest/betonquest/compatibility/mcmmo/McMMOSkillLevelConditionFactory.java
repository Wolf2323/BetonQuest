package org.betonquest.betonquest.compatibility.mcmmo;

import com.gmail.nossr50.datatypes.skills.PrimarySkillType;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link McMMOSkillLevelCondition}s from {@link Instruction}s.
 */
public class McMMOSkillLevelConditionFactory implements PlayerConditionFactory {

    /**
     * Create a new factory for mc mmo level conditions.
     */
    public McMMOSkillLevelConditionFactory() {
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<PrimarySkillType> skillType = instruction.enumeration(PrimarySkillType.class).get();
        final Argument<Number> level = instruction.number().get();
        return new OnlineConditionAdapter(new McMMOSkillLevelCondition(skillType, level));
    }
}
