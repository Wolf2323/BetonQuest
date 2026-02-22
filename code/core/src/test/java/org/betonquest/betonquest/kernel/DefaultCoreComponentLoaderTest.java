package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
    void loading_before_registration_fails() {
        final RawDummyComponent another = spy(new RawDummyComponent(false));
        another.loadComponent(new DependencyProvider() {
            @Override
            public <U> void take(final Class<U> type, final U dependency) {
                // Empty
            }
        });
        assertThrows(IllegalArgumentException.class, () -> loader.register(another));
    }

    @Test
    void manually_loading_after_registration_fails_in_load_method_with_warning() {
        final RawDummyComponent another = spy(new RawDummyComponent(false));
        loader.register(another);
        another.loadComponent(new DependencyProvider() {
            @Override
            public <U> void take(final Class<U> type, final U dependency) {
                // Empty
            }
        });
        loader.load();
        verify(logger, atLeastOnce()).warn(anyString());
        verify(another, atMostOnce()).load(any());
    }

    @Test
    void loading_with_empty_component_list_does_nothing() {
        loader.load();
        assertThrows(NoSuchElementException.class, () -> loader.get(Object.class), "Should throw an exception because there are no components loaded");
        assertEquals(0, this.loader.getAll(Object.class).size(), "Should contain no components");
    }

    @Test
    void loading_initial_injections_should_also_provide_them_as_instances() {
        loader.init(BetonQuestLogger.class, logger);
        assertThrows(NoSuchElementException.class, () -> loader.get(Object.class), "Should throw an exception because there are no components loaded");
        loader.load();
        assertEquals(logger, loader.get(BetonQuestLogger.class), "Should contain the initialized logger");
    }

    @Test
    void loading_initial_injections_multiple_times_fails() {
        loader.init(BetonQuestLogger.class, logger);
        assertThrows(IllegalStateException.class, () -> loader.init(BetonQuestLogger.class, logger));
    }

    @Test
    void loading_component_to_check_change_in_availability() {
        final CoreComponent component = spy(new RawDummyComponent(true));
        loader.register(component);
        assertThrows(NoSuchElementException.class, () -> loader.get(RawDummyComponent.class), "Component should not be loaded before loading");
        loader.load();
        assertEquals(component, loader.get(RawDummyComponent.class), "Component should be loaded after loading");
    }

    @Test
    void loading_component_to_check_result() {
        final CoreComponent component = spy(new RawDummyComponent());
        loader.register(component);
        loader.load();
        verify(component, times(1)).loadComponent(loader);
        assertTrue(component.isLoaded(), "Component should be loaded after loading");
    }

    @Test
    void can_load_method_works_properly() {
        final CoreComponent component = spy(new RawDummyComponent());
        loader.register(component);
        assertTrue(component.canLoad(), "Component should be loadable before loading");
        loader.load();
        assertFalse(component.canLoad(), "Component should not be loadable after loading");
    }

    @Test
    void loading_a_component_with_unavailable_dependency_fails() {
        final CoreComponent component = spy(new RawDummyComponent(RawDummyComponent.class));
        loader.register(component);
        assertThrows(IllegalStateException.class, loader::load, "Should throw an exception because a blocking component that cannot be loaded");
        verify(component, never()).loadComponent(loader);
    }

    @Nested
    class FullDependencies {

        private static final Consumer<DependencyProvider> STRING = provider -> provider.take(String.class, "");

        private static final Consumer<DependencyProvider> BOOLEAN = provider -> provider.take(Boolean.class, true);

        private static final Consumer<DependencyProvider> INTEGER = provider -> provider.take(Integer.class, 1);

        private static Stream<Arguments> failDependencies() {
            return Stream.of(
                    Arguments.of(Set.of(new RawDummyComponent(true, RawDummyComponent.class))),
                    Arguments.of(Set.of(new RawDummyComponent(), new RawDummyComponent(RawDummyComponent.class))),
                    Arguments.of(Set.of(new RawDummyComponent(STRING, Boolean.class), new RawDummyComponent(BOOLEAN, String.class))),
                    Arguments.of(Set.of(new RawDummyComponent(true, Integer.class), new RawDummyComponent(STRING, RawDummyComponent.class),
                            new RawDummyComponent(BOOLEAN, String.class), new RawDummyComponent(INTEGER, Boolean.class))),
                    Arguments.of(Set.of(new RawDummyComponent(true), new RawDummyComponent(true, RawDummyComponent.class)))
            );
        }

        private static Stream<Arguments> dependencies() {
            return Stream.of(
                    Arguments.of(0, Set.of()),
                    Arguments.of(1, Set.of(new RawDummyComponent(true))),
                    Arguments.of(0, Set.of(new RawDummyComponent(false))),
                    Arguments.of(2, Set.of(new RawDummyComponent(true), new RawDummyComponent(STRING, RawDummyComponent.class))),
                    Arguments.of(3, Set.of(new RawDummyComponent(true), new RawDummyComponent(STRING, RawDummyComponent.class), new RawDummyComponent(BOOLEAN, String.class))),
                    Arguments.of(4, Set.of(new RawDummyComponent(true), new RawDummyComponent(STRING, RawDummyComponent.class), new RawDummyComponent(BOOLEAN, String.class),
                            new RawDummyComponent(INTEGER, Boolean.class))),
                    Arguments.of(0, aLot(16)),
                    Arguments.of(0, aLot(100)),
                    Arguments.of(0, aLot(1000))
            );
        }

        private static List<RawDummyComponent> aLot(final int amount) {
            final List<RawDummyComponent> components = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                components.add(new RawDummyComponent());
            }
            return components;
        }

        @ParameterizedTest
        @MethodSource("dependencies")
        void test_working_dependencies(final int loadedObjectsCount, final Collection<RawDummyComponent> components) {
            components.forEach(loader::register);
            loader.load();
            assertEquals(loadedObjectsCount, loader.getAll(Object.class).size(), String.format("Should contain exactly %d components", loadedObjectsCount));
            assertTrue(components.stream().allMatch(CoreComponent::isLoaded), "All components should be loaded");
        }

        @ParameterizedTest
        @MethodSource("failDependencies")
        void test_failing_dependencies(final Collection<RawDummyComponent> components) {
            components.forEach(loader::register);
            assertThrows(IllegalStateException.class, loader::load, "Should throw an exception because a component should fail to load");
            assertFalse(components.stream().allMatch(CoreComponent::isLoaded), "Not all components should be loaded");
        }
    }

    @Nested
    class Dependency {

        private AbstractCoreComponent dummyDependency;

        private AbstractCoreComponent dummyComponent;

        @BeforeEach
        void setUp() {
            dummyDependency = spy(new RawDummyComponent(RawDummyComponent.class, BetonQuestLogger.class));
            dummyComponent = spy(new RawDummyComponent(true));
            loader.register(dummyDependency);
            loader.register(dummyComponent);
        }

        @Test
        void loading_with_initial_logger_injection_to_check_components_load_calls() {
            loader.init(BetonQuestLogger.class, logger);
            loader.load();
            verify(dummyDependency, times(1)).load(loader);
            verify(dummyComponent, times(1)).load(loader);
        }

        @Test
        void loading_without_initial_dependencies_fails() {
            assertThrows(IllegalStateException.class, loader::load, "Should throw an exception because a component should fail to load without dependencies");
            verify(dummyDependency, never()).load(loader);
        }

        @Test
        void loading_without_initial_logger_should_still_load_components_before_that() {
            assertThrows(IllegalStateException.class, loader::load, "Should throw an exception because a component should fail to load without logger");
            assertEquals(1, loader.getAll(CoreComponent.class).size(), "Should contain exactly one component");
        }

        @Test
        void loading_correctly_to_check_results() {
            loader.init(BetonQuestLogger.class, logger);
            loader.load();
            assertEquals(1, loader.getAll(RawDummyComponent.class).size(), "Should contain exactly one component");
        }

        @Test
        void loading_correctly_to_test_get_all_method() {
            loader.init(BetonQuestLogger.class, logger);
            loader.load();
            assertEquals(1, loader.getAll(CoreComponent.class).size(), "Should contain exactly one component");
            assertEquals(2, loader.getAll(Object.class).size(), "Should contain all dependencies");
        }
    }
}
