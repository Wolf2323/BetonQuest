package org.betonquest.betonquest.config.patcher;

import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.config.DefaultConfigAccessorFactory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ConfigPatcherIT {
    @Test
    void patch_old_config_to_new_config(@TempDir final Path tempDir) throws IOException, InvalidConfigurationException {
        final File config = new File("src/main/resources/config.yml");
        final File configPatch = new File("src/main/resources/config.patch.yml");
        final File oldConfig = new File("src/test/resources/config/oldConfigToUpdate.yml");

        final File pluginConfig = new File(tempDir.toFile(), "config.yml");
        Files.copy(oldConfig.toPath(), pluginConfig.toPath());

        final BetonQuestLoggerFactory loggerFactory = mock(BetonQuestLoggerFactory.class);
        when(loggerFactory.create(any(Class.class))).thenReturn(mock(BetonQuestLogger.class));

        final Plugin plugin = mock(Plugin.class);
        when(plugin.getResource("config.yml")).thenReturn(new FileInputStream(config));
        when(plugin.getResource("config.yml.patch")).thenReturn(new FileInputStream(configPatch));

        final DefaultConfigAccessorFactory configAccessorFactory = new DefaultConfigAccessorFactory(loggerFactory, loggerFactory.create(ConfigAccessorFactory.class));
        configAccessorFactory.createPatching(pluginConfig, plugin, "config.yml");

        final YamlConfiguration yamlPluginConfig = new YamlConfiguration();
        final YamlConfiguration yamlPatchedConfig = new YamlConfiguration();
        yamlPluginConfig.load(new FileReader(config));
        yamlPatchedConfig.load(new FileReader(pluginConfig));

        yamlPatchedConfig.set("configVersion", "");
        assertConfigContains("", yamlPluginConfig, yamlPatchedConfig);
        assertConfigContains("", yamlPatchedConfig, yamlPluginConfig);
    }

    private void assertConfigContains(final String parentKey, final ConfigurationSection actual, final ConfigurationSection contains) {
        for (final String key : contains.getKeys(true)) {
            if (contains.isConfigurationSection(key)) {
                assertTrue(actual.isConfigurationSection(key), "Key '" + key + "' is missing in the actual config");
                assertConfigContains(parentKey + "." + key, actual.getConfigurationSection(key), contains.getConfigurationSection(key));
            } else {
                assertTrue(actual.contains(key), "Key '" + key + "' is missing in the actual config");
                assertEquals(contains.get(key), actual.get(key), "Key '" + parentKey + "." + key + "' has different value in the actual config");
            }
        }
    }
}
