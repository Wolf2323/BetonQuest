package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.kernel.dependency.DependencyHelper;
import org.betonquest.betonquest.kernel.dependency.LoadedDependency;

import java.util.HashSet;
import java.util.Set;

/**
 * Default abstract implementation of {@link CoreComponent} aiming to reduce boilerplate code.
 */
public abstract class AbstractCoreComponent implements CoreComponent {

    /**
     * The injected dependencies of this component.
     */
    protected final Set<LoadedDependency<?>> injectedDependencies;

    /**
     * Whether this component was already loaded.
     */
    private boolean loaded;

    /**
     * Create a new component.
     */
    public AbstractCoreComponent() {
        this.injectedDependencies = new HashSet<>();
    }

    @Override
    public void inject(final LoadedDependency<?> dependency) {
        if (requires(dependency.type())) {
            this.injectedDependencies.add(dependency);
        }
    }

    @Override
    public Set<Class<?>> provides() {
        return Set.of();
    }

    @Override
    public boolean canLoad() {
        return !isLoaded() && DependencyHelper.remainingDependencies(requires(), injectedDependencies).isEmpty();
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public boolean requires(final Class<?> type) {
        return DependencyHelper.isStillRequired(requires(), injectedDependencies, type);
    }

    @Override
    public final void loadComponent(final DependencyProvider dependencyProvider) {
        load(dependencyProvider);
        this.loaded = true;
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

    @Override
    public abstract Set<Class<?>> requires();

    /**
     * Implement all loading logic here and use the provided {@link DependencyProvider} to inject dependencies into
     * all other components.
     *
     * @param dependencyProvider the dependency provider to use
     */
    protected abstract void load(DependencyProvider dependencyProvider);
}
