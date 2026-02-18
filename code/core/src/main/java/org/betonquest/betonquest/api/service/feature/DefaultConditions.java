package org.betonquest.betonquest.api.service.feature;

import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.condition.ConditionRegistry;
import org.betonquest.betonquest.api.service.condition.Conditions;

/**
 * Default implementation of the {@link Conditions} service.
 *
 * @param manager  the condition manager
 * @param registry the condition registry
 */
public record DefaultConditions(ConditionManager manager, ConditionRegistry registry) implements Conditions {

}
