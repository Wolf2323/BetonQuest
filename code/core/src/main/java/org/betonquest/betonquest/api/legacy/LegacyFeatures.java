package org.betonquest.betonquest.api.legacy;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.identifier.CompassIdentifier;
import org.betonquest.betonquest.api.identifier.JournalEntryIdentifier;
import org.betonquest.betonquest.api.identifier.JournalMainPageIdentifier;
import org.betonquest.betonquest.api.identifier.QuestCancelerIdentifier;
import org.betonquest.betonquest.api.quest.npc.DefaultNpcHider;
import org.betonquest.betonquest.api.text.Text;
import org.betonquest.betonquest.feature.QuestCanceler;
import org.betonquest.betonquest.feature.QuestCompass;
import org.betonquest.betonquest.feature.journal.JournalMainPageEntry;

import java.util.Map;

/**
 * The FeatureApi provides access to more complex features, often based on basic features.
 */
public interface LegacyFeatures {

    /**
     * Get the loaded Quest Canceler.
     *
     * @return quest cancelers in a new map
     */
    Map<QuestCancelerIdentifier, QuestCanceler> getCancelers();

    /**
     * Gets stored Quest Canceler.
     *
     * @param cancelerID the compass id
     * @return the loaded QuestCanceler
     * @throws QuestException if no QuestCanceler is loaded for the ID
     */
    QuestCanceler getCanceler(QuestCancelerIdentifier cancelerID) throws QuestException;

    /**
     * Get the loaded Compasses.
     *
     * @return compasses in a new map
     */
    Map<CompassIdentifier, QuestCompass> getCompasses();

    /**
     * Gets stored Journal Entry.
     *
     * @param journalEntryID the journal entry id
     * @return the loaded text
     * @throws QuestException if no text is loaded for the ID
     */
    Text getJournalEntry(JournalEntryIdentifier journalEntryID) throws QuestException;

    /**
     * Renames the Journal Entry instance.
     *
     * @param name   the current name
     * @param rename the name it should have now
     */
    void renameJournalEntry(JournalEntryIdentifier name, JournalEntryIdentifier rename);

    /**
     * Get the loaded Journal Main Page Entries.
     *
     * @return pages in a new map
     */
    Map<JournalMainPageIdentifier, JournalMainPageEntry> getJournalMainPages();

    /**
     * Gets the NpcHider.
     *
     * @return the active npc hider
     */
    DefaultNpcHider getNpcHider();
}
