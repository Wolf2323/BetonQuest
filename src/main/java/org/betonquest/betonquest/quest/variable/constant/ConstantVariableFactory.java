package org.betonquest.betonquest.quest.variable.constant;

import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.variable.PlayerVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerVariableFactory;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariable;
import org.betonquest.betonquest.api.quest.variable.PlayerlessVariableFactory;
import org.betonquest.betonquest.api.quest.variable.nullable.NullableVariableAdapter;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.quest.registry.processor.VariableProcessor;
import org.bukkit.configuration.ConfigurationSection;

/**
 * A factory for creating Eval variables.
 */
public class ConstantVariableFactory implements PlayerVariableFactory, PlayerlessVariableFactory {
    /**
     * Variable processor that the constant variable should use for creating variables.
     */
    private final VariableProcessor variableProcessor;

    /**
     * Create a new Eval variable factory.
     *
     * @param variableProcessor variable processor to use
     */
    public ConstantVariableFactory(final VariableProcessor variableProcessor) {
        this.variableProcessor = variableProcessor;
    }

    @Override
    public PlayerVariable parsePlayer(final Instruction instruction) throws QuestException {
        return parseConstantVariable(instruction);
    }

    @Override
    public PlayerlessVariable parsePlayerless(final Instruction instruction) throws QuestException {
        return parseConstantVariable(instruction);
    }

    private NullableVariableAdapter parseConstantVariable(final Instruction instruction) throws QuestException {
        final ConfigurationSection section = instruction.getPackage().getConfig().getConfigurationSection("constants");
        if (section == null) {
            throw new QuestException("No 'constants' section found in the QuestPackage!");
        }
        final String constantTarget = instruction.next();
        final String constant = section.getString(constantTarget);
        if (constant == null) {
            throw new QuestException("No constant with the name '" + constantTarget + "' found in the 'constants' section!");
        }
        return new NullableVariableAdapter(new ConstantVariable(new VariableString(variableProcessor, instruction.getPackage(), constant)));
    }
}
