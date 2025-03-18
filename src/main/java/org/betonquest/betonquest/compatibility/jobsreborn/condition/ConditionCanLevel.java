package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.JobProgression;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.compatibility.jobsreborn.VariableJob;

/**
 * Condition to check if the player can level up the profession.
 */
public class ConditionCanLevel implements PlayerCondition {

    /**
     * Job to check.
     */
    private final VariableJob job;

    /**
     * Create a new can level condition.
     *
     * @param job the job to check
     */
    public ConditionCanLevel(final VariableJob job) {
        this.job = job;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final JobProgression progression = Jobs.getPlayerManager().getJobsPlayer(profile.getPlayerUUID()).getJobProgression(job.getValue(profile));
        return progression != null && progression.canLevelUp();
    }
}
