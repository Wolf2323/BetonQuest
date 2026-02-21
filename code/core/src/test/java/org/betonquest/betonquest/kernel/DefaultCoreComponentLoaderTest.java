package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class DefaultCoreComponentLoaderTest {

    private DefaultCoreComponentLoader loader;

    @Mock
    private BetonQuestLogger logger;

    @BeforeEach
    void setUp() {
        this.loader = spy(new DefaultCoreComponentLoader(logger));
    }

    @Test
    void start_with_empty_component_list() {
        loader.load();
        assertThrows(NoSuchElementException.class, () -> loader.get(Object.class), "Should throw an exception because there are no components loaded");
        assertEquals(0, this.loader.getAll(Object.class).size(), "Should contain no components");
    }

    @Test
    void load_initial_injections() {
        loader.init(BetonQuestLogger.class, logger);
        assertThrows(NoSuchElementException.class, () -> loader.get(Object.class), "Should throw an exception because there are no components loaded");
        loader.load();
        assertEquals(logger, loader.get(BetonQuestLogger.class), "Should contain the initialized logger");
    }

    @Test
    void load_component_with_dependencies() {
        final DummyComponent component = spy(new DummyComponent());
        loader.register(component);
        assertThrows(NoSuchElementException.class, () -> loader.get(DummyComponent.class), "Component should not be loaded before loading");
        loader.load();
        assertEquals(component, loader.get(DummyComponent.class), "Component should be loaded after loading");
    }

    @Test
    void load_component() {
        final DummyComponent component = spy(new DummyComponent());
        loader.register(component);
        loader.load();
        verify(component, times(1)).load(loader);
        assertTrue(component.isLoaded(), "Component should be loaded after loading");
    }

    @Test
    void can_load_component() {
        final DummyComponent component = spy(new DummyComponent());
        loader.register(component);
        assertTrue(component.canLoad(), "Component should be loadable before loading");
        loader.load();
        assertFalse(component.canLoad(), "Component should not be loadable after loading");
    }

    @Test
    void load_blocking_component() {
        final DummyComponent component = spy(new DummyComponent());
        component.blocking = true;
        loader.register(component);
        assertThrows(IllegalStateException.class, loader::load, "Should throw an exception because a blocking component that cannot be loaded");
        verify(component, never()).load(loader);
    }

    private static final class DummyDependency implements CoreComponent {

        private boolean gotTest;

        private boolean gotLogger;

        private boolean loaded;

        @Override
        public Set<Class<?>> requires() {
            return Set.of(DummyComponent.class, BetonQuestLogger.class);
        }

        @Override
        public boolean requires(final Class<?> type) {
            return true;
        }

        @Override
        public void inject(final LoadedDependency<?> loadedDependency) {
            if (loadedDependency.match(DummyComponent.class)) {
                gotTest = true;
            }
            if (loadedDependency.match(BetonQuestLogger.class)) {
                gotLogger = true;
            }
        }

        @Override
        public boolean canLoad() {
            return gotTest && gotLogger && !loaded;
        }

        @Override
        public boolean isLoaded() {
            return loaded;
        }

        @Override
        public void load(final DependencyProvider dependencyProvider) {
            dependencyProvider.take(DummyDependency.class, this);
            loaded = true;
        }
    }

    private static final class DummyComponent implements CoreComponent {

        private boolean loaded;

        private boolean blocking;

        @Override
        public Set<Class<?>> requires() {
            return Set.of();
        }

        @Override
        public boolean requires(final Class<?> type) {
            return false;
        }

        @Override
        public void inject(final LoadedDependency<?> loadedDependency) {
            // Empty
        }

        @Override
        public boolean canLoad() {
            return !loaded && !blocking;
        }

        @Override
        public boolean isLoaded() {
            return loaded;
        }

        @Override
        public void load(final DependencyProvider dependencyProvider) {
            dependencyProvider.take(DummyComponent.class, this);
            loaded = true;
        }
    }

    @Nested
    class Dependency {

        private DummyDependency dummyDependency;

        private DummyComponent dummyComponent;

        @BeforeEach
        void setUp() {
            dummyDependency = spy(new DummyDependency());
            dummyComponent = spy(new DummyComponent());
            loader.register(dummyDependency);
            loader.register(dummyComponent);
        }

        @Test
        void load_with_logger_init() {
            loader.init(BetonQuestLogger.class, logger);
            loader.load();
            verify(dummyDependency, times(1)).load(loader);
            verify(dummyComponent, times(1)).load(loader);
        }

        @Test
        void load_without_dependency_init() {
            assertThrows(IllegalStateException.class, loader::load, "Should throw an exception because a component should fail to load without dependencies");
            verify(dummyDependency, never()).load(loader);
        }

        @Test
        void load_without_logger_init_instances() {
            assertThrows(IllegalStateException.class, loader::load);
            assertEquals(1, loader.getAll(CoreComponent.class).size(), "Should contain exactly one component");
        }

        @Test
        void load_check_results() {
            loader.init(BetonQuestLogger.class, logger);
            loader.load();
            assertEquals(dummyDependency, loader.get(DummyDependency.class), "Should contain TestDependency");
            assertEquals(dummyComponent, loader.get(DummyComponent.class), "Should contain TestComponent");
        }

        @Test
        void get_all_elements() {
            loader.init(BetonQuestLogger.class, logger);
            loader.load();
            assertEquals(2, loader.getAll(CoreComponent.class).size(), "Should contain exactly two components");
            assertEquals(3, loader.getAll(Object.class).size(), "Should contain all dependencies");
        }
    }
}
