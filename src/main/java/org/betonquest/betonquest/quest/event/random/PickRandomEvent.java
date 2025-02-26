package org.betonquest.betonquest.quest.event.random;

import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.api.quest.event.nullable.NullableEvent;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Fires randomly events by chance and amount.
 */
public class PickRandomEvent implements NullableEvent {
    /**
     * The events with there chance.
     */
    private final List<RandomEvent> events;

    /**
     * The amount of events to fire.
     */
    @Nullable
    private final VariableNumber amount;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * Creates a new PickRandomEvent.
     *
     * @param events       the events with there chance
     * @param amount       the amount of events to fire
     * @param questTypeAPI the Quest Type API
     */
    public PickRandomEvent(final List<RandomEvent> events, @Nullable final VariableNumber amount, final QuestTypeAPI questTypeAPI) {
        this.events = events;
        this.amount = amount;
        this.questTypeAPI = questTypeAPI;
    }

    @Override
    public void execute(@Nullable final Profile profile) throws QuestException {
        final List<ResolvedRandomEvent> resolvedEvents = new LinkedList<>();
        for (final RandomEvent randomEvent : events) {
            resolvedEvents.add(randomEvent.resolveFor(profile));
        }
        double total = resolvedEvents.stream().mapToDouble(ResolvedRandomEvent::chance).sum();

        int pick = this.amount == null ? 1 : this.amount.getValue(profile).intValue();
        while (pick > 0 && !resolvedEvents.isEmpty()) {
            pick--;
            double random = Math.random() * total;
            final Iterator<ResolvedRandomEvent> iterator = resolvedEvents.iterator();
            while (iterator.hasNext()) {
                final ResolvedRandomEvent event = iterator.next();
                random -= event.chance();
                if (random < 0) {
                    questTypeAPI.event(profile, event.eventID());
                    iterator.remove();
                    total -= event.chance();
                    break;
                }
            }
        }
    }
}
