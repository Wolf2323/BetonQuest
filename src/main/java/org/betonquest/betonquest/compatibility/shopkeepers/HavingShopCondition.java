package org.betonquest.betonquest.compatibility.shopkeepers;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import com.nisovin.shopkeepers.api.shopkeeper.player.PlayerShopkeeper;
import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.Condition;
import org.betonquest.betonquest.api.profiles.Profile;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.instruction.variable.VariableNumber;

/**
 * Checks if the player owns specified amount of shops.
 */
@SuppressWarnings("PMD.CommentRequired")
public class HavingShopCondition extends Condition {

    private final VariableNumber amount;

    public HavingShopCondition(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        persistent = true;
        amount = instruction.getVarNum();
    }

    @Override
    protected Boolean execute(final Profile profile) throws QuestRuntimeException {
        int count = amount.getInt(profile);
        for (final Shopkeeper s : ShopkeepersAPI.getShopkeeperRegistry().getAllShopkeepers()) {
            if (s instanceof final PlayerShopkeeper shopkeeper && profile.getProfileUUID().equals(shopkeeper.getOwnerUUID())) {
                count--;
                if (count == 0) {
                    return true;
                }
            }
        }
        return false;
    }

}
