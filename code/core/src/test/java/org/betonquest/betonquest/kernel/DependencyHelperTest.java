package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.kernel.component.ActionsComponent;
import org.betonquest.betonquest.kernel.component.ArgumentParsersComponent;
import org.betonquest.betonquest.kernel.component.AsyncSaverComponent;
import org.betonquest.betonquest.kernel.component.CancelersComponent;
import org.betonquest.betonquest.kernel.component.ConditionsComponent;
import org.betonquest.betonquest.kernel.component.FontRegistryComponent;
import org.betonquest.betonquest.kernel.component.InstructionsComponent;
import org.betonquest.betonquest.kernel.component.ItemsComponent;
import org.betonquest.betonquest.kernel.component.JournalsComponent;
import org.betonquest.betonquest.kernel.component.NotificationsComponent;
import org.betonquest.betonquest.kernel.component.NpcsComponent;
import org.betonquest.betonquest.kernel.component.ObjectivesComponent;
import org.betonquest.betonquest.kernel.component.PlaceholdersComponent;
import org.betonquest.betonquest.kernel.component.PlayerDataStorageComponent;
import org.betonquest.betonquest.kernel.component.PluginMessageComponent;
import org.betonquest.betonquest.kernel.component.ProfileProviderComponent;
import org.betonquest.betonquest.kernel.component.UpdaterComponent;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class DependencyHelperTest {

    //a list of random component classes
    private static final List<Class<?>> RANDOM_CLASSES_TO_PICK = new ArrayList<>(List.of(ActionsComponent.class,
            ConditionsComponent.class, ObjectivesComponent.class, PlaceholdersComponent.class, ArgumentParsersComponent.class,
            AsyncSaverComponent.class, CancelersComponent.class, InstructionsComponent.class, FontRegistryComponent.class,
            ItemsComponent.class, NotificationsComponent.class, PluginMessageComponent.class, JournalsComponent.class,
            NpcsComponent.class, UpdaterComponent.class, PlayerDataStorageComponent.class, ProfileProviderComponent.class));

    private static final List<LoadedDependency<?>> LOADED_DEPENDENCIES = RANDOM_CLASSES_TO_PICK.stream().<LoadedDependency<?>>map(DependencyHelperTest::dep).toList();

    private static <T> LoadedDependency<T> dep(final Class<T> clazz) {
        return new LoadedDependency<>(clazz, mock(clazz));
    }

    private static <T> List<T> random(final List<T> list) {
        final Random random = new Random();
        final int first = random.nextInt(list.size());
        int second = random.nextInt(list.size());
        while (first == second) {
            second = random.nextInt(list.size());
        }
        return list.subList(Math.min(first, second), Math.max(first, second));
    }

    private static Stream<Arguments> combinations() {
        final List<Arguments> arguments = new ArrayList<>(RANDOM_CLASSES_TO_PICK.size() * 5);
        for (int i = 0; i < RANDOM_CLASSES_TO_PICK.size() * 5; i++) {
            arguments.add(Arguments.of(random(RANDOM_CLASSES_TO_PICK), random(LOADED_DEPENDENCIES)));
        }
        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource("combinations")
    void remaining_requirements_are_disjoint_from_loaded_dependency(final List<Class<?>> requirements, final List<LoadedDependency<?>> loadedDependencies) {
        final Set<Class<?>> classes = DependencyHelper.remainingDependencies(requirements, loadedDependencies);
        final boolean classesDisjointFromLoaded = Collections.disjoint(classes, loadedDependencies.stream().map(LoadedDependency::type).collect(Collectors.toSet()));
        assertTrue(classesDisjointFromLoaded, "Remaining requirements should be disjoint from loaded: %s vs. %s"
                .formatted(classes.stream().map(Class::getSimpleName).toList(), loadedDependencies.stream().map(LoadedDependency::type).map(Class::getSimpleName).toList()));
    }

    @ParameterizedTest
    @MethodSource("combinations")
    void remaining_classes_are_still_required(final List<Class<?>> requirements, final List<LoadedDependency<?>> loadedDependencies) {
        final Set<Class<?>> classes = DependencyHelper.remainingDependencies(requirements, loadedDependencies);
        assertTrue(classes.stream().allMatch(cl -> DependencyHelper.isStillRequired(requirements, loadedDependencies, cl)), "Class should be still required");
    }

    @ParameterizedTest
    @MethodSource("combinations")
    void loaded_dependency_are_not_required(final List<Class<?>> requirements, final List<LoadedDependency<?>> loadedDependencies) {
        final boolean anyLoadedStillRequired = loadedDependencies.stream().map(LoadedDependency::type)
                .anyMatch(loaded -> DependencyHelper.isStillRequired(requirements, loadedDependencies, loaded));
        assertFalse(anyLoadedStillRequired, "Class should not be required");
    }

    @Test
    void subclasses_of_dependencies_are_also_required() {
        assertTrue(DependencyHelper.isRequired(List.of(CoreComponent.class), AbstractCoreComponent.class),
                "Subclasses of dependencies should be required");
        assertFalse(DependencyHelper.isRequired(List.of(AbstractCoreComponent.class), CoreComponent.class),
                "Superclasses of dependencies should not be required");
    }

    @Test
    void subclasses_of_dependencies_are_not_required_if_loaded() {
        assertTrue(DependencyHelper.isStillRequired(List.of(CoreComponent.class), List.of(), AbstractCoreComponent.class),
                "Subclasses of dependencies should be required if not loaded");
        assertFalse(DependencyHelper.isStillRequired(List.of(CoreComponent.class), List.of(dep(CoreComponent.class)), AbstractCoreComponent.class),
                "Subclasses of dependencies should not be required if the superclass is loaded");
    }
}
