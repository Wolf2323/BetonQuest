package org.betonquest.betonquest.api.service.feature;

import org.betonquest.betonquest.api.service.placeholder.PlaceholderManager;
import org.betonquest.betonquest.api.service.placeholder.PlaceholderRegistry;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;

/**
 * Default implementation of the {@link Placeholders} service.
 *
 * @param manager  the placeholder manager
 * @param registry the placeholder registry
 */
public record DefaultPlaceholders(PlaceholderManager manager, PlaceholderRegistry registry) implements Placeholders {

}
