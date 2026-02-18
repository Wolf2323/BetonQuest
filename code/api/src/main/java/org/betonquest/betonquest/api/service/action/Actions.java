package org.betonquest.betonquest.api.service.action;

import org.betonquest.betonquest.api.service.ServiceFeature;

/**
 * Register new actions with the {@link ActionRegistry} or execute existing ones with the {@link ActionManager}.
 */
public interface Actions extends ServiceFeature<ActionManager, ActionRegistry> {

}
