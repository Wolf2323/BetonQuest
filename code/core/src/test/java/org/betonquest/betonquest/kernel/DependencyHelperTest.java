package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.kernel.component.ActionsComponent;
import org.betonquest.betonquest.kernel.component.ArgumentParsersComponent;
import org.betonquest.betonquest.kernel.component.AsyncSaverComponent;
import org.betonquest.betonquest.kernel.component.CancelersComponent;
import org.betonquest.betonquest.kernel.component.ConditionsComponent;
import org.betonquest.betonquest.kernel.component.FontRegistryComponent;
import org.betonquest.betonquest.kernel.component.InstructionsComponent;
import org.betonquest.betonquest.kernel.component.ObjectivesComponent;
import org.betonquest.betonquest.kernel.component.PlaceholdersComponent;
import org.betonquest.betonquest.kernel.dependency.DependencyHelper;
import org.betonquest.betonquest.kernel.dependency.LoadedDependency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DependencyHelperTest {

    private static final List<Class<?>> RANDOM_CLASSES_TO_PICK = new ArrayList<>(List.of(ActionsComponent.class,
            ConditionsComponent.class, ObjectivesComponent.class, PlaceholdersComponent.class, ArgumentParsersComponent.class,
            AsyncSaverComponent.class, CancelersComponent.class, InstructionsComponent.class, FontRegistryComponent.class));

    private static final List<LoadedDependency<?>> LOADED_DEPENDENCIES = new ArrayList<>(RANDOM_CLASSES_TO_PICK.stream().<LoadedDependency<?>>map(DependencyHelperTest::dep).toList());

    private static <T> LoadedDependency<T> dep(final Class<T> clazz) {
        return new LoadedDependency<>(clazz, mock(clazz));
    }

    private static Stream<Arguments> combinations() {
        final List<Arguments> arguments = new ArrayList<>();
        SimpleSubSetHelper.allKSubSets(RANDOM_CLASSES_TO_PICK).forEach(subSet -> {
            SimpleSubSetHelper.allKSubSets(LOADED_DEPENDENCIES).forEach(loadedSubSet -> {
                arguments.add(Arguments.of(subSet, loadedSubSet));
            });
        });
        return arguments.stream();
    }

    @ParameterizedTest
    @MethodSource("combinations")
    void remaining_requirements_are_disjoint_from_loaded_dependency(final Collection<Class<?>> requirements, final Collection<LoadedDependency<?>> loadedDependencies) {
        final Set<Class<?>> classes = DependencyHelper.remainingDependencies(requirements, loadedDependencies);
        final boolean classesDisjointFromLoaded = Collections.disjoint(classes, loadedDependencies.stream().map(LoadedDependency::type).collect(Collectors.toSet()));
        assertTrue(classesDisjointFromLoaded, "Remaining requirements should be disjoint from loaded: %s vs. %s"
                .formatted(classes.stream().map(Class::getSimpleName).toList(), loadedDependencies.stream().map(LoadedDependency::type).map(Class::getSimpleName).toList()));
    }

    @ParameterizedTest
    @MethodSource("combinations")
    void remaining_classes_are_still_required(final Collection<Class<?>> requirements, final Collection<LoadedDependency<?>> loadedDependencies) {
        final Set<Class<?>> classes = DependencyHelper.remainingDependencies(requirements, loadedDependencies);
        assertTrue(classes.stream().allMatch(cl -> DependencyHelper.isStillRequired(requirements, loadedDependencies, cl)), "Class should be still required");
    }

    @ParameterizedTest
    @MethodSource("combinations")
    void loaded_dependency_are_not_required(final Collection<Class<?>> requirements, final Collection<LoadedDependency<?>> loadedDependencies) {
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
