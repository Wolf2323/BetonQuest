package org.betonquest.betonquest.api.instruction;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.ItemIdentifier;
import org.betonquest.betonquest.api.instruction.type.ItemWrapper;
import org.betonquest.betonquest.api.item.QuestItem;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.service.item.ItemManager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Wrapper for {@link QuestItem} to also store target stack amount.
 */
@SuppressWarnings("PMD.ShortClassName")
public class Item implements ItemWrapper {

    /**
     * The item manager to retrieve items from.
     */
    private final ItemManager itemManager;

    /**
     * Item id to generate the QuestItem with.
     */
    private final ItemIdentifier itemID;

    /**
     * Size of the stack to create.
     */
    private final Argument<Number> amount;

    /**
     * Create a wrapper for Quest Item and target stack size.
     *
     * @param itemManager the item manager to retrieve items from
     * @param itemID      the QuestItemID to create
     * @param amount      the size to set the created ItemStack to
     */
    public Item(final ItemManager itemManager, final ItemIdentifier itemID, final Argument<Number> amount) {
        this.itemManager = itemManager;
        this.itemID = itemID;
        this.amount = amount;
    }

    @Override
    public ItemStack generate(@Nullable final Profile profile) throws QuestException {
        return getItem(profile).generate(amount.getValue(profile).intValue(), profile);
    }

    @Override
    public boolean matches(@Nullable final ItemStack item, @Nullable final Profile profile) throws QuestException {
        return getItem(profile).matches(item);
    }

    @Override
    public ItemIdentifier getID() {
        return itemID;
    }

    @Override
    public QuestItem getItem(@Nullable final Profile profile) throws QuestException {
        return itemManager.getItem(profile, itemID);
    }

    @Override
    public Argument<Number> getAmount() {
        return amount;
    }
}
