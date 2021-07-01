package org.betonquest.betonquest.compatibility.fakeblock;

import org.betonquest.betonquest.Instruction;
import org.betonquest.betonquest.api.QuestEvent;
import org.betonquest.betonquest.exceptions.InstructionParseException;
import org.betonquest.betonquest.exceptions.QuestRuntimeException;
import org.betonquest.betonquest.utils.BlockSelector;
import org.betonquest.betonquest.utils.PlayerConverter;
import org.betonquest.betonquest.utils.location.CompoundLocation;
import org.bukkit.Location;
import pro.husk.fakeblock.objects.FakeBlockData;
import pro.husk.fakeblock.objects.LatestMaterialWall;

import java.util.HashMap;

/**
 * Displays an effect.
 */
@SuppressWarnings("PMD.CommentRequired")
public class FakeBlockEvent extends QuestEvent {
    private final BlockSelector selector;
    private final CompoundLocation loc;

    public FakeBlockEvent(final Instruction instruction) throws InstructionParseException {
        super(instruction, true);
        staticness = true;
        persistent = true;
        selector = instruction.getBlockSelector(instruction.next());
        loc = instruction.getLocation();
    }

    @Override
    protected Void execute(final String playerID) throws QuestRuntimeException {
        final Location location = loc.getLocation(playerID);
        final LatestMaterialWall latestMaterialWall = new LatestMaterialWall(instruction.getID().getFullID());
        latestMaterialWall.setLocation1(location);
        latestMaterialWall.setLocation2(location);

        final FakeBlockData fakeBlock = new FakeBlockData(selector.getBlockData());
        final HashMap<Location, FakeBlockData> fakeBlockDataHashMap = new HashMap<>();
        fakeBlockDataHashMap.put(location, fakeBlock);
        latestMaterialWall.createNonPersistentWall(fakeBlockDataHashMap);

        latestMaterialWall.addUserToDisplayFor(PlayerConverter.getPlayer(playerID));

        return null;
    }

}
