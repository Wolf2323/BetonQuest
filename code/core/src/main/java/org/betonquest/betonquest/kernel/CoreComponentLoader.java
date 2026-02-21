package org.betonquest.betonquest.kernel;

/**
 * The core component loader essentially manages a number of {@link CoreComponent}s and loads them the order
 * their dependencies suggest. It detects unsolvable dependencies and throws an exception in case of any.
 */
public interface CoreComponentLoader {

    /**
     * Registers a new core component to be loaded in {@link #load()} later on.
     * The registered component may not be yet loaded.
     *
     * @param component the component to register
     */
    void register(CoreComponent component);

    /**
     * Registers a new instance of a dependency that was loaded before this component loader.
     * It will be initially injected into the components that require it before they are loaded.
     *
     * @param type     the class of the dependency
     * @param instance the instance of the dependency
     * @param <T>      the type of the dependency
     */
    <T> void init(Class<T> type, T instance);

    /**
     * Loads all registered components in the correct order as their dependencies suggest.
     * May throw an exception in case of unsolvable dependencies causing the loading to abort.
     */
    void load();
}
