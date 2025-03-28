package org.betonquest.betonquest.quest.event.tag;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.event.PlayerEvent;
import org.betonquest.betonquest.api.quest.event.PlayerEventFactory;
import org.betonquest.betonquest.api.quest.event.PlayerlessEvent;
import org.betonquest.betonquest.api.quest.event.PlayerlessEventFactory;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.util.Utils;

import java.util.Locale;

/**
 * Factory to create global tag events from {@link Instruction}s.
 */
public class TagGlobalEventFactory implements PlayerEventFactory, PlayerlessEventFactory {
    /**
     * BetonQuest instance to provide to events.
     */
    private final BetonQuest betonQuest;

    /**
     * Create the global tag event factory.
     *
     * @param betonQuest BetonQuest instance to pass on
     */
    public TagGlobalEventFactory(final BetonQuest betonQuest) {
        this.betonQuest = betonQuest;
    }

    @Override
    public PlayerEvent parsePlayer(final Instruction instruction) throws QuestException {
        final String action = instruction.next();
        final String[] tags = getTags(instruction);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "add" -> createAddTagEvent(tags);
            case "delete", "del" -> createDeleteTagEvent(tags);
            default -> throw new QuestException("Unknown tag action: " + action);
        };
    }

    @Override
    public PlayerlessEvent parsePlayerless(final Instruction instruction) throws QuestException {
        final String action = instruction.next();
        final String[] tags = getTags(instruction);
        return switch (action.toLowerCase(Locale.ROOT)) {
            case "add" -> createStaticAddTagEvent(tags);
            case "delete", "del" -> createStaticDeleteTagEvent(tags);
            default -> throw new QuestException("Unknown tag action: " + action);
        };
    }

    private String[] getTags(final Instruction instruction) throws QuestException {
        final String[] tags;
        tags = instruction.getArray();
        for (int ii = 0; ii < tags.length; ii++) {
            tags[ii] = Utils.addPackage(instruction.getPackage(), tags[ii]);
        }
        return tags;
    }

    private PlayerlessEvent createStaticAddTagEvent(final String... tags) {
        final TagChanger tagChanger = new AddTagChanger(tags);
        return new PlayerlessTagEvent(betonQuest.getGlobalData(), tagChanger);
    }

    private PlayerlessEvent createStaticDeleteTagEvent(final String... tags) {
        final TagChanger tagChanger = new DeleteTagChanger(tags);
        return new PlayerlessTagEvent(betonQuest.getGlobalData(), tagChanger);
    }

    private PlayerEvent createAddTagEvent(final String... tags) {
        final TagChanger tagChanger = new AddTagChanger(tags);
        return new TagEvent(profile -> betonQuest.getGlobalData(), tagChanger);
    }

    private PlayerEvent createDeleteTagEvent(final String... tags) {
        final TagChanger tagChanger = new DeleteTagChanger(tags);
        return new TagEvent(profile -> betonQuest.getGlobalData(), tagChanger);
    }
}
