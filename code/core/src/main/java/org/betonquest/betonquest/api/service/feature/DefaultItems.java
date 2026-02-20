package org.betonquest.betonquest.api.service.feature;

import org.betonquest.betonquest.api.service.item.ItemManager;
import org.betonquest.betonquest.api.service.item.ItemRegistry;
import org.betonquest.betonquest.api.service.item.Items;

/**
 * Default implementation of the {@link Items} service.
 *
 * @param manager  the item manager
 * @param registry the item registry
 */
public record DefaultItems(ItemManager manager, ItemRegistry registry) implements Items {

}
