package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class AbstractCoreComponentTest {

    private AbstractCoreComponent component;

    @Mock
    private BetonQuestLogger logger;

    private CoreComponentLoader loader;

    @BeforeEach
    void setUp() {
        component = spy(new RawDummyComponent(BetonQuestLogger.class));
        loader = new DefaultCoreComponentLoader(logger);
    }

    @Test
    void requirement_fulfilled() {
        assertTrue(component.requires(BetonQuestLogger.class), "Component should require BetonQuestLogger");
        component.inject(new LoadedDependency<>(BetonQuestLogger.class, logger));
        assertFalse(component.requires(BetonQuestLogger.class), "Component should not require BetonQuestLogger anymore");
    }

    @Test
    void requirement_can_load() {
        assertFalse(component.canLoad(), "Component should not be loadable before injecting dependencies");
        component.inject(new LoadedDependency<>(BetonQuestLogger.class, logger));
        assertTrue(component.canLoad(), "Component should be loadable after injecting dependencies");
    }

    @Test
    void loading_with_missing_dependencies() {
        loader.register(component);
        assertThrows(IllegalStateException.class, loader::load, "Component should not be loadable without dependencies");
        verify(component, never()).load(any());
    }

    @Test
    void loading_without_fulfilled_dependencies() {
        component.inject(new LoadedDependency<>(BetonQuestLogger.class, logger));
        loader.register(component);
        loader.load();
        verify(component, times(1)).load(any());
        assertTrue(component.isLoaded(), "Component should be loaded after loading");
    }

    @Test
    void get_dependency_with_fulfilled_requirement() {
        component.inject(new LoadedDependency<>(BetonQuestLogger.class, logger));
        assertEquals(logger, component.getDependency(BetonQuestLogger.class), "Should return injected dependency");
    }

    @Test
    void get_missing_dependency() {
        assertThrows(NoSuchElementException.class, () -> component.getDependency(BetonQuestLogger.class), "Should throw exception on missing dependency");
    }
}


