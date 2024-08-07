package org.betonquest.betonquest.conditions;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.location.VariableLocation;

/**
 * Checks Y height player is at (must be below)
 */
@SuppressWarnings("PMD.CommentRequired")
public class HeightCondition extends Condition {

    private final VariableNumber height;

    public HeightCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        final String string = instruction.next();
        final QuestPackage pack = instruction.getPackage();
        if (string.matches("-?\\d+\\.?\\d*")) {
            try {
                height = new VariableNumber(pack, string);
            } catch (final InstructionParseException e) {
                throw new InstructionParseException("Could not parse height", e);
            }
        } else {
            try {
                height = new VariableNumber(pack, String.valueOf(new VariableLocation(BetonQuest.getInstance().getVariableProcessor(), pack, string).getValue(null).getY()));
            } catch (final QuestRuntimeException e) {
                throw new InstructionParseException("Could not parse height", e);
            }
        }
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        return profile.getOnlineProfile().get().getPlayer().getLocation().getY() < height.getValue(profile).doubleValue();
    }

}
