package org.betonquest.betonquest.api.service.feature;

import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.action.ActionRegistry;
import org.betonquest.betonquest.api.service.action.Actions;

/**
 * Default implementation of the {@link Actions} service.
 *
 * @param manager  the action manager
 * @param registry the action registry
 */
public record DefaultActions(ActionManager manager, ActionRegistry registry) implements Actions {

}
