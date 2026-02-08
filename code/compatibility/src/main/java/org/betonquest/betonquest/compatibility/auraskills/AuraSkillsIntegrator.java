package org.betonquest.betonquest.compatibility.auraskills;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.quest.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.BetonQuestRegistries;
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
        final BetonQuestRegistries questRegistries = api.registries();

        questRegistries.actions().register("auraskillsxp", new AuraSkillsExperienceActionFactory(auraSkillsApi));

        final ConditionRegistry conditionRegistry = questRegistries.conditions();
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
