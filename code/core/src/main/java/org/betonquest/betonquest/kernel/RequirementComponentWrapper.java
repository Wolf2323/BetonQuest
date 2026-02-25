package org.betonquest.betonquest.kernel;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Wrapper for {@link CoreComponent} to add additional requirements.
 */
public class RequirementComponentWrapper extends AbstractCoreComponent {

    /**
     * The wrapped component.
     */
    private final CoreComponent wrappedComponent;

    /**
     * The additional requirements.
     */
    private final Set<Class<?>> additionalRequirements;

    /**
     * Create a new wrapper for the specified component with additional requirements.
     *
     * @param wrappedComponent       the wrapped component
     * @param additionalRequirements the additional requirements
     */
    public RequirementComponentWrapper(final CoreComponent wrappedComponent, final Class<?>... additionalRequirements) {
        super();
        this.wrappedComponent = wrappedComponent;
        this.additionalRequirements = Set.of(additionalRequirements);
    }

    @Override
    public Set<Class<?>> requires() {
        final Set<Class<?>> requirements = new LinkedHashSet<>(wrappedComponent.requires());
        requirements.addAll(additionalRequirements);
        return requirements;
    }

    @Override
    public void inject(final LoadedDependency<?> dependency) {
        super.inject(dependency);
        wrappedComponent.inject(dependency);
    }

    @Override
    public boolean canLoad() {
        return super.canLoad() && wrappedComponent.canLoad();
    }

    @Override
    public boolean isLoaded() {
        return super.isLoaded() && wrappedComponent.isLoaded();
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        wrappedComponent.loadComponent(dependencyProvider);
    }
}
