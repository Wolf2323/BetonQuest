package org.betonquest.betonquest.kernel;

/**
 * The record representing an injected dependency.
 *
 * @param type       the type of the dependency
 * @param dependency the dependency instance itself
 * @param <T>        the generic type of the dependency
 */
public record LoadedDependency<T>(Class<T> type, T dependency) {

    /**
     * Matches a type against the dependency type.
     *
     * @param type the type to match
     * @return if the dependency type is assignable to the given type
     */
    public boolean match(final Class<?> type) {
        return type.isAssignableFrom(this.type);
    }
}
