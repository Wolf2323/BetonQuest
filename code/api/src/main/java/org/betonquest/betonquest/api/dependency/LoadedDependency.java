package org.betonquest.betonquest.api.dependency;

/**
 * Represents a loaded dependency to be injected.
 *
 * @param <T> the type of the dependency
 */
public interface LoadedDependency<T> {

    /**
     * Matches a type against the dependency type.
     *
     * @param type the type to match
     * @return if the dependency type is assignable to the given type
     */
    boolean match(Class<?> type);

    /**
     * Gets the dependency type.
     *
     * @return the dependency type
     */
    Class<T> type();

    /**
     * Gets the dependency instance.
     *
     * @return the dependency instance
     */
    T dependency();
}
