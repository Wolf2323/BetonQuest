package org.betonquest.betonquest.compatibility.heroes.action;

import com.herocraftonline.heroes.characters.CharacterManager;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.instruction.Argument;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.quest.action.OnlineActionAdapter;
import org.betonquest.betonquest.api.quest.action.PlayerAction;
import org.betonquest.betonquest.api.quest.action.PlayerActionFactory;
import org.betonquest.betonquest.compatibility.heroes.HeroesClassType;

/**
 * Factory to create {@link HeroesExperienceAction}s from {@link Instruction}s.
 */
public class HeroesExperienceActionFactory implements PlayerActionFactory {

    /**
     * The {@link CharacterManager} of the Heroes plugin.
     */
    private final CharacterManager characterManager;

    /**
     * Create a new Factory to create Give Brew Actions.
     *
     * @param characterManager the {@link CharacterManager} of the Heroes plugin.
     */
    public HeroesExperienceActionFactory(final CharacterManager characterManager) {
        this.characterManager = characterManager;
    }

    @Override
    public PlayerAction parsePlayer(final Instruction instruction) throws QuestException {
        final Argument<HeroesClassType> classType = instruction.enumeration(HeroesClassType.class).get();
        final Argument<Number> amount = instruction.number().get();

        return new OnlineActionAdapter(new HeroesExperienceAction(characterManager, classType, amount));
    }
}
