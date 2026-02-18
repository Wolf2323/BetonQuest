package org.betonquest.betonquest.api.service.feature;

import org.betonquest.betonquest.api.service.objective.ObjectiveManager;
import org.betonquest.betonquest.api.service.objective.ObjectiveRegistry;
import org.betonquest.betonquest.api.service.objective.Objectives;

/**
 * Default implementation of the {@link Objectives} service.
 *
 * @param manager  the objective manager
 * @param registry the objective registry
 */
public record DefaultObjectives(ObjectiveManager manager, ObjectiveRegistry registry) implements Objectives {

}
