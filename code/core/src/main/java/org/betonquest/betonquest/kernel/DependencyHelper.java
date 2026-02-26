package org.betonquest.betonquest.kernel;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helps with handling all kinds of dependency-related filtration and analysis.
 */
final class DependencyHelper {

    private DependencyHelper() {
    }

    /**
     * Filters the remaining dependencies from the given required and loaded dependencies.
     *
     * @param required the required dependencies
     * @param loaded   the loaded dependencies
     * @return the remaining dependencies
     */
    /* default */
    static Set<Class<?>> remainingDependencies(final Collection<Class<?>> required, final Collection<LoadedDependency<?>> loaded) {
        return required.stream()
                .filter(requirement -> loaded.stream().noneMatch(dependency -> dependency.match(requirement)))
                .collect(Collectors.toSet());
    }

    /**
     * Checks if the given dependency is required by the given required dependencies.
     * An instance is required if it is assignable to any of the required dependencies.
     *
     * @param requiredDependencies the required dependencies
     * @param instanceType         the type of the instance to check for requiredness
     * @return true if the instance is required, false otherwise
     */
    /* default */
    static boolean isRequired(final Collection<Class<?>> requiredDependencies, final Class<?> instanceType) {
        return requiredDependencies.stream().anyMatch(requirement -> requirement.isAssignableFrom(instanceType));
    }

    /**
     * Checks if the given dependency is still required by the given required dependencies if all loaded dependencies
     * are factored out.
     *
     * @param requiredDependencies the required dependencies
     * @param loadedDependencies   the loaded dependencies
     * @param instanceType         the type of the instance to check for requiredness
     * @return true if the instance is still required, false otherwise
     */
    /* default */
    static boolean isStillRequired(final Collection<Class<?>> requiredDependencies,
                                   final Collection<LoadedDependency<?>> loadedDependencies, final Class<?> instanceType) {
        return isRequired(remainingDependencies(requiredDependencies, loadedDependencies), instanceType);
    }
}
