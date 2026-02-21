package org.betonquest.betonquest.kernel;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Default abstract implementation of {@link CoreComponent} aiming to reduce boilerplate code.
 */
public abstract class AbstractCoreComponent implements CoreComponent {

    /**
     * The injected dependencies of this component.
     */
    protected final Set<LoadedDependency<?>> injectedDependencies;

    /**
     * Whether to disable the dependency injection filter.
     * See {@link #AbstractCoreComponent(boolean)} for more information.
     */
    private final boolean disableDependencyInjectionFilter;

    /**
     * Create a new component.
     */
    public AbstractCoreComponent() {
        this(false);
    }

    /**
     * Create a new component and optionally disable the dependency injection filter.
     * The dependency injection filter in {@link #inject(LoadedDependency)} ensures that only dependencies
     * that are required by this component are injected - disabling will lead to accepting all injected dependencies.
     *
     * @param disableDependencyInjectionFilter whether to disable the dependency injection filter
     */
    public AbstractCoreComponent(final boolean disableDependencyInjectionFilter) {
        this.injectedDependencies = new HashSet<>();
        this.disableDependencyInjectionFilter = disableDependencyInjectionFilter;
    }

    @Override
    public void inject(final LoadedDependency<?> dependency) {
        if (disableDependencyInjectionFilter || requires(dependency.type())) {
            this.injectedDependencies.add(dependency);
        }
    }

    @Override
    public boolean canLoad() {
        return !isLoaded() && remainingRequirements().findAny().isEmpty();
    }

    @Override
    public boolean requires(final Class<?> type) {
        return remainingRequirements().anyMatch(required -> required.isAssignableFrom(type));
    }

    /**
     * Creates a stream of all classes that are required as defined by #requires() but are not yet injected.
     *
     * @return a stream of remaining required classes
     */
    private Stream<Class<?>> remainingRequirements() {
        return requires().stream().filter(requirement -> injectedDependencies.stream()
                .noneMatch(dependency -> dependency.match(requirement)));
    }

    /**
     * Get a dependency of this component.
     *
     * @param type the {@link Class} of the dependency to get
     * @param <U>  the type of the dependency
     * @return the dependency
     */
    protected <U> U getDependency(final Class<U> type) {
        final LoadedDependency<?> injectedDependency = injectedDependencies.stream()
                .filter(dependency -> dependency.match(type)).findFirst().orElseThrow();
        return type.cast(injectedDependency.dependency());
    }
}
