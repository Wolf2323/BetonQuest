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
        assertThrows(NoSuchElementException.class, () -> loader.get(Object.class));
        assertEquals(0, this.loader.getAll(Object.class).size());
    }

    @Test
    void load_initial_injections() {
        loader.init(BetonQuestLogger.class, logger);
        assertThrows(NoSuchElementException.class, () -> loader.get(Object.class));
        loader.load();
        assertEquals(logger, loader.get(BetonQuestLogger.class));
    }

    @Test
    void load_component_with_dependencies() {
        final TestComponent component = spy(new TestComponent());
        loader.register(component);
        assertThrows(NoSuchElementException.class, () -> loader.get(TestComponent.class));
        loader.load();
        assertEquals(component, loader.get(TestComponent.class));
    }

    @Test
    void load_component() {
        final TestComponent component = spy(new TestComponent());
        loader.register(component);
        loader.load();
        verify(component, times(1)).load(loader);
        assertTrue(component.isLoaded());
    }

    @Test
    void can_load_component() {
        final TestComponent component = spy(new TestComponent());
        loader.register(component);
        assertTrue(component.canLoad());
        loader.load();
        assertFalse(component.canLoad());
    }

    @Test
    void load_blocking_component() {
        final TestComponent component = spy(new TestComponent());
        component.blocking = true;
        loader.register(component);
        assertThrows(IllegalStateException.class, () -> loader.load());
        verify(component, never()).load(loader);
    }

    @Nested
    class Dependency {

        private TestDependency dependency;

        private TestComponent component;

        @BeforeEach
        void setUp() {
            dependency = spy(new TestDependency());
            component = spy(new TestComponent());
            loader.register(dependency);
            loader.register(component);
        }

        @Test
        void load_with_logger_init() {
            loader.init(BetonQuestLogger.class, logger);
            loader.load();
            verify(dependency, times(1)).load(loader);
            verify(component, times(1)).load(loader);
        }

        @Test
        void load_without_logger_init() {
            assertThrows(IllegalStateException.class, () -> loader.load());
            verify(dependency, never()).load(loader);
            verify(component, times(1)).load(loader);
        }

        @Test
        void load_without_logger_init_instances() {
            assertThrows(IllegalStateException.class, () -> loader.load());
            assertEquals(1, loader.getAll(CoreComponent.class).size());
            assertEquals(component, loader.get(TestComponent.class));
        }

        @Test
        void load_check_results() {
            loader.init(BetonQuestLogger.class, logger);
            loader.load();
            assertEquals(dependency, loader.get(TestDependency.class));
            assertEquals(component, loader.get(TestComponent.class));
        }

        @Test
        void get_all_elements() {
            loader.init(BetonQuestLogger.class, logger);
            loader.load();
            assertEquals(2, loader.getAll(CoreComponent.class).size());
            assertEquals(3, loader.getAll(Object.class).size());
        }
    }

    class TestDependency implements CoreComponent {

        private boolean gotTest = false;

        private boolean gotLogger = false;

        private boolean loaded = false;

        @Override
        public Set<Class<?>> requires() {
            return Set.of(TestComponent.class, BetonQuestLogger.class);
        }

        @Override
        public boolean requires(final Class<?> type) {
            return true;
        }

        @Override
        public void inject(final LoadedDependency<?> loadedDependency) {
            if (loadedDependency.match(TestComponent.class)) {
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
            dependencyProvider.take(TestDependency.class, this);
            loaded = true;
        }
    }

    class TestComponent implements CoreComponent {

        private boolean loaded = false;

        private boolean blocking = false;

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
            dependencyProvider.take(TestComponent.class, this);
            loaded = true;
        }
    }
}
