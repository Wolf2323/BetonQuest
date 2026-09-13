package org.betonquest.betonquest.quest.action.conversation;

import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.instruction.Instruction;
import org.betonquest.betonquest.api.instruction.InstructionMock;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.lib.config.DefaultConfigAccessorFactory;
import org.betonquest.betonquest.lib.config.quest.QuestPackageImpl;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerExtension;
import org.bukkit.configuration.InvalidConfigurationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for {@link CancelConversationActionFactory}.
 */
@ExtendWith({BetonQuestLoggerExtension.class, MockitoExtension.class})
class CancelConversationActionFactoryIT {

    private QuestPackage setupQuestPackage(final BetonQuestLoggerFactory loggerFactory,
                                           final BetonQuestLogger logger,
                                           final Path questPackagesDirectory)
            throws IOException, InvalidConfigurationException {
        final Path packageDirectory = questPackagesDirectory.resolve("test");
        if (!packageDirectory.toFile().mkdir()) {
            throw new IOException("Failed to create test package directory.");
        }
        final File packageConfigFile = packageDirectory.resolve("package.yml").toFile();
        if (!packageConfigFile.createNewFile()) {
            throw new IOException("Failed to create test package main configuration file.");
        }
        return new QuestPackageImpl(logger, new DefaultConfigAccessorFactory(loggerFactory, logger), "test",
                packageConfigFile, Collections.emptyList());
    }

    @Test
    void accepts_empty_instruction(final BetonQuestLoggerFactory loggerFactory,
                                   final BetonQuestLogger logger,
                                   @TempDir final Path questPackagesDirectory,
                                   @Mock final Conversations conversations)
            throws IOException, InvalidConfigurationException, QuestException {
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);
        final Instruction instruction = new InstructionMock(questPackage, "cancelconversation");

        assertDoesNotThrow(() -> new CancelConversationActionFactory(conversations).parsePlayer(instruction));
    }

    @Test
    void accepts_skip_delay_flag(final BetonQuestLoggerFactory loggerFactory,
                                 final BetonQuestLogger logger,
                                 @TempDir final Path questPackagesDirectory,
                                 @Mock final Conversations conversations)
            throws IOException, InvalidConfigurationException, QuestException {
        final QuestPackage questPackage = setupQuestPackage(loggerFactory, logger, questPackagesDirectory);
        final Instruction instruction = new InstructionMock(questPackage, "cancelconversation skipDelay");

        assertDoesNotThrow(() -> new CancelConversationActionFactory(conversations).parsePlayer(instruction));
    }
}
