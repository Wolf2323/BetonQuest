package org.betonquest.betonquest.kernel;

/**
 * Provides dependencies to the core components in the {@link CoreComponent#load(DependencyProvider)} method and
 * distributes them across all components in the {@link CoreComponentLoader}.
 */
@FunctionalInterface
public interface DependencyProvider {

    /**
     * Provides a new loaded dependency to the core components.
     *
     * @param type       the type of the dependency
     * @param dependency the dependency instance
     * @param <U>        the type of the dependency
     */
    <U> void take(Class<U> type, U dependency);
}
