package org.betonquest.betonquest.kernel;

import java.util.Set;

/**
 * A core component of the BetonQuest plugin representing a unique unit of functionality that may be loaded
 * by a {@link CoreComponentLoader} respecting its dependencies and thereby being loaded in order.
 */
public interface CoreComponent {

    /**
     * Specifies the types that this component requires to be loaded before it.
     * Essentially contains all classes that are dependencies of this component.
     *
     * @return all dependencies of this component
     */
    Set<Class<?>> requires();

    /**
     * Checks whether this component still requires the specified type.
     * May be used to check whether a dependency has been already injected.
     * <br>
     * Generally refers to dependencies defined {@link #requires()}.
     *
     * @param type the type to check
     * @return if this component still requires the specified type
     */
    boolean requires(Class<?> type);

    /**
     * Injects a loaded dependency into this component and removes it from the list of required dependencies.
     *
     * @param loadedDependency the dependency instance to inject
     */
    void inject(LoadedDependency<?> loadedDependency);

    /**
     * Checks whether this component can be loaded.
     * This may be true if all required dependencies have been injected and shall never be true if the component
     * is already loaded {@link #isLoaded()}.
     *
     * @return if this component can be loaded
     */
    boolean canLoad();

    /**
     * Checks whether this component has been loaded.
     *
     * @return if this component has been loaded
     */
    boolean isLoaded();

    /**
     * Loads this component.
     * <br> <br>
     * A successful loading process should result in {@link #isLoaded()} returning true.
     * A failed loading process should result in {@link #isLoaded()} still returning false and throwing an exception.
     * <br> <br>
     * The loaded dependencies shall be provided using the specified dependency provider
     * to distribute them to all components.
     *
     * @param dependencyProvider the dependency provider to use for loading dependencies
     */
    void load(DependencyProvider dependencyProvider);
}
