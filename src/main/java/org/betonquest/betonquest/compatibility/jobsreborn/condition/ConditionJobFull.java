package org.betonquest.betonquest.compatibility.jobsreborn.condition;

import com.gamingmesh.jobs.Jobs;
import com.gamingmesh.jobs.container.Job;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.condition.PlayerCondition;
import org.betonquest.betonquest.compatibility.jobsreborn.VariableJob;

/**
 * Condition to check if a player has the max slots in a job reached.
 */
public class ConditionJobFull implements PlayerCondition {

    /**
     * Job to check.
     */
    private final VariableJob job;

    /**
     * Create a new job full condition.
     *
     * @param job the job to check
     */
    public ConditionJobFull(final VariableJob job) {
        this.job = job;
    }

    @Override
    public boolean check(final Profile profile) throws QuestException {
        final Job resolvedJob = this.job.getValue(profile);
        for (final Job job : Jobs.getJobs()) {
            if (job.isSame(resolvedJob)) {
                if (job.getMaxSlots() == null) {
                    return false;
                }
                if (job.getTotalPlayers() >= job.getMaxSlots()) {
                    return true;
                }
            }
        }
        return false;
    }
}
