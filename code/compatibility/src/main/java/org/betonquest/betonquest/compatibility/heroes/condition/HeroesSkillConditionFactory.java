package org.betonquest.betonquest.compatibility.heroes.condition;

import com.herocraftonline.heroes.characters.CharacterManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.condition.OnlineConditionAdapter;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.api.quest.condition.PlayerConditionFactory;

/**
 * Factory to create {@link HeroesSkillCondition}s from {@link Instruction}s.
 */
public class HeroesSkillConditionFactory implements PlayerConditionFactory {

    /**
     * The {@link CharacterManager} of the Heroes plugin.
     */
    private final CharacterManager characterManager;

    /**
     * Create a new Factory to create Give Brew Events.
     *
     * @param characterManager the {@link CharacterManager} of the Heroes plugin.
     */
    public HeroesSkillConditionFactory(final CharacterManager characterManager) {
        this.characterManager = characterManager;
    }

    @Override
    public PlayerCondition parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<String> skillName = instruction.string().get();
        return new OnlineConditionAdapter(new HeroesSkillCondition(characterManager, skillName));
    }
}
