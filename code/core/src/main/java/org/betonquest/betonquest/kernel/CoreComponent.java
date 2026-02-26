package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.kernel.dependency.DependencyGraphNode;

/**
 * A core component of the BetonQuest plugin representing a unique unit of functionality that may be loaded
 * by a {@link CoreComponentLoader} respecting its dependencies and thereby being loaded in order.
 * <br> <br>
 * Every {@link CoreComponent} defines a list of dependencies in {@link #requires()} that must be injected
 * via {@link #inject(LoadedDependency)} before the component can be loaded (see {@link #canLoad()}).
 * <br> <br>
 * The loading process essentially follows a few steps:
 * <ul>
 *     <li>The {@link #loadComponent(DependencyProvider)} initiates the loading process expecting all dependencies to be
 *     injected and may throw an exception if an invalid state occurs.</li>
 *     <li>During loading the component may choose to provide instances other components depend on via the
 *     {@link DependencyProvider} provided as parameter.</li>
 *     <li>After the loading process has finished successfully, {@link #isLoaded()} returns true</li>
 * </ul>
 */
public interface CoreComponent extends DependencyGraphNode {

    /**
     * Checks whether this component still requires the specified type.
     * May be used to check whether a dependency has been already injected.
     * <br>
     * Generally refers to dependencies defined in {@link #requires()}.
     *
     * @param type the type to check
     * @return if this component still requires the specified type
     */
    boolean requires(Class<?> type);

    /**
     * Injects a loaded dependency into this component and removes it from the list of required dependencies.
     * Injecting a dependency not required as defined by {@link #requires(Class)} will have no effect.
     *
     * @param loadedDependency the dependency instance to inject
     */
    void inject(LoadedDependency<?> loadedDependency);

    /**
     * Checks whether this component can be loaded.
     * <br> <br>
     * This may be true if all required dependencies have been injected. <br>
     * It must never be true if the component is already loaded ({@link #isLoaded()} is true).
     *
     * @return if this component can be loaded
     */
    boolean canLoad();

    /**
     * Checks whether this component has been loaded.
     * <br> <br>
     * This method must return true after #loadComponent() has been called successfully.
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
     * @throws IllegalStateException if an invalid state occurs during loading
     */
    void loadComponent(DependencyProvider dependencyProvider);
}
