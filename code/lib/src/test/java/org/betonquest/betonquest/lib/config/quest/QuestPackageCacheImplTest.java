package org.betonquest.betonquest.lib.config.quest;

import org.betonquest.betonquest.api.config.FileConfigAccessor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link QuestPackageCacheImpl}.
 */
@ExtendWith(MockitoExtension.class)
class QuestPackageCacheImplTest {

    @Mock
    private FileConfigAccessor accessor;

    private YamlConfiguration config;

    private QuestPackageCacheImpl cache;

    @BeforeEach
    void setUp() {
        config = new YamlConfiguration();
        when(accessor.getConfig()).thenReturn(config);
        cache = new QuestPackageCacheImpl(accessor);
    }

    @Test
    void testSave() throws IOException {
        when(accessor.save()).thenReturn(true);
        assertTrue(cache.save());
        verify(accessor).save();
    }

    @Test
    void testDelete() throws IOException {
        when(accessor.delete()).thenReturn(true);
        assertTrue(cache.delete());
        verify(accessor).delete();
    }

    @Test
    void testReload() throws IOException {
        cache.reload();
        verify(accessor).reload();
    }

    @Test
    void testGetConfigurationFile() {
        final File file = new File("test.yml");
        when(accessor.getConfigurationFile()).thenReturn(file);
        assertEquals(file, cache.getConfigurationFile());
    }

    @Test
    void testGetConfig() {
        assertEquals(config, cache.getConfig());
    }

    @Test
    void testSetAndGet() {
        cache.set("key", "value");
        assertEquals("value", cache.getString("key"));
    }
}
