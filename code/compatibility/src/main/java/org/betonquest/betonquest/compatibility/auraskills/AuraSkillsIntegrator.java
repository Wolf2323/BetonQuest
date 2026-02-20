package org.betonquest.betonquest.compatibility.auraskills;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.auraskills.action.AuraSkillsExperienceActionFactory;
import org.betonquest.betonquest.compatibility.auraskills.condition.AuraSkillsLevelConditionFactory;
import org.betonquest.betonquest.compatibility.auraskills.condition.AuraSkillsStatsConditionFactory;

/**
 * Integrator for <a href="https://github.com/Archy-X/AuraSkills">AuraSkills</a>.
 */
public class AuraSkillsIntegrator implements Integrator {

    /**
     * The default constructor.
     */
    public AuraSkillsIntegrator() {
    }

    @Override
    public void hook(final BetonQuestApi api) {
        final AuraSkillsApi auraSkillsApi = AuraSkillsApi.get();

        api.actions().registry().register("auraskillsxp", new AuraSkillsExperienceActionFactory(auraSkillsApi));

        final ConditionRegistry conditionRegistry = api.conditions().registry();
        conditionRegistry.register("auraskillslevel", new AuraSkillsLevelConditionFactory(auraSkillsApi));
        conditionRegistry.register("auraskillsstatslevel", new AuraSkillsStatsConditionFactory(auraSkillsApi));
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }
}
