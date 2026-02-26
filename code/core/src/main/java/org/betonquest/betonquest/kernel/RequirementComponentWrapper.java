package org.betonquest.betonquest.kernel;

import java.util.HashSet;
import java.util.Set;

/**
 * Wrapper for {@link CoreComponent} to add additional requirements.
 */
public class RequirementComponentWrapper implements CoreComponent {

    /**
     * The wrapped component.
     */
    private final CoreComponent wrappedComponent;

    /**
     * The additional requirements.
     */
    private final Set<Class<?>> requirements;

    /**
     * The loaded requirements.
     */
    private final Set<LoadedDependency<?>> loadedRequirements;

    /**
     * Create a new wrapper for the specified component with additional requirements.
     *
     * @param wrappedComponent       the wrapped component
     * @param additionalRequirements the additional requirements
     */
    public RequirementComponentWrapper(final CoreComponent wrappedComponent, final Class<?>... additionalRequirements) {
        super();
        this.wrappedComponent = wrappedComponent;
        this.requirements = new HashSet<>();
        this.requirements.addAll(wrappedComponent.requires());
        this.requirements.addAll(Set.of(additionalRequirements));
        this.loadedRequirements = new HashSet<>();
    }

    @Override
    public Set<Class<?>> requires() {
        return requirements;
    }

    @Override
    public Set<Class<?>> provides() {
        return wrappedComponent.provides();
    }

    @Override
    public boolean requires(final Class<?> type) {
        return DependencyHelper.isStillRequired(requires(), loadedRequirements, type);
    }

    @Override
    public void inject(final LoadedDependency<?> dependency) {
        if (requires(dependency.type())) {
            loadedRequirements.add(dependency);
            wrappedComponent.inject(dependency);
        }
    }

    @Override
    public boolean canLoad() {
        return !isLoaded() && DependencyHelper.remainingDependencies(requires(), loadedRequirements).isEmpty();
    }

    @Override
    public boolean isLoaded() {
        return wrappedComponent.isLoaded();
    }

    @Override
    public void loadComponent(final DependencyProvider dependencyProvider) {
        wrappedComponent.loadComponent(dependencyProvider);
    }
}
