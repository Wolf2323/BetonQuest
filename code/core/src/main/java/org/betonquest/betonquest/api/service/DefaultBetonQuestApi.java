package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.config.quest.QuestPackageManager;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.service.action.Actions;
import org.betonquest.betonquest.api.service.condition.Conditions;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.identifier.Identifiers;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.item.Items;
import org.betonquest.betonquest.api.service.npc.Npcs;
import org.betonquest.betonquest.api.service.objective.Objectives;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;

/**
 * The default implementation of the {@link BetonQuestApi}.
 *
 * @param profiles      the profile provider handling profiles for players
 * @param packages      the package manager for quest packages
 * @param loggerFactory the logger factory to create loggers for individual services
 * @param instructions  the instruction api accessor
 * @param actions       the actions api accessor
 * @param conditions    the conditions api accessor
 * @param objectives    the objectives api accessor
 * @param placeholders  the placeholders api accessor
 * @param items         the item api accessor
 * @param npcs          the npc api accessor
 * @param conversations the conversation api accessor
 * @param identifiers   the identifier api accessor
 */
public record DefaultBetonQuestApi(ProfileProvider profiles, QuestPackageManager packages,
                                   BetonQuestLoggerFactory loggerFactory, Instructions instructions, Actions actions,
                                   Conditions conditions, Objectives objectives, Placeholders placeholders, Items items,
                                   Npcs npcs, Conversations conversations,
                                   Identifiers identifiers) implements BetonQuestApi {

}
