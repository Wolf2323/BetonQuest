package org.betonquest.betonquest.quest.placeholder.objective;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ObjectiveIdentifier;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.objective.Objective;
import org.betonquest.betonquest.api.quest.objective.service.ObjectiveService;
import org.betonquest.betonquest.api.quest.placeholder.PlayerPlaceholder;
import org.betonquest.betonquest.api.service.ObjectiveManager;

/**
 * Resolves to a specified property of an objective.
 */
public class ObjectivePropertyPlaceholder implements PlayerPlaceholder {

    /**
     * The objective manager.
     */
    private final ObjectiveManager objectiveManager;

    /**
     * The objective ID.
     */
    private final ObjectiveIdentifier objectiveID;

    /**
     * The property name.
     */
    private final String propertyName;

    /**
     * Create a new objective property placeholder.
     *
     * @param objectiveManager The objective manager.
     * @param objectiveID      The objective ID.
     * @param propertyName     The property name.
     */
    public ObjectivePropertyPlaceholder(final ObjectiveManager objectiveManager, final ObjectiveIdentifier objectiveID, final String propertyName) {
        this.objectiveManager = objectiveManager;
        this.objectiveID = objectiveID;
        this.propertyName = propertyName;
    }

    @Override
    public String getValue(final Profile profile) throws QuestException {
        final Objective objective = objectiveManager.getObjective(objectiveID);
        final ObjectiveService service = objective.getService();
        if (service.containsProfile(profile)) {
            return service.getProperties().getProperty(propertyName, profile);
        }
        return "";
    }
}
