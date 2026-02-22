package org.betonquest.betonquest.kernel;

import java.util.Set;
import java.util.function.Consumer;

/**
 * Dummy component for testing purposes.
 */
public class RawDummyComponent extends AbstractCoreComponent {

    /**
     * The dependencies of this component.
     */
    private final Set<Class<?>> dependencies;

    /**
     * The method to call when loading this component.
     */
    private final Consumer<DependencyProvider> loadMethod;

    /**
     * Wether this component should inject itself into the dependency provider.
     */
    private final boolean injectSelf;

    /**
     * Whether this component is loaded.
     */
    private boolean loaded;

    /**
     * Create a new dummy component.
     *
     * @param dependencies the dependencies of this component
     */
    public RawDummyComponent(final Class<?>... dependencies) {
        this(false, dependencies);
    }

    /**
     * Create a new dummy component.
     *
     * @param injectSelf   wether this component should inject itself into the dependency provider
     * @param dependencies the dependencies of this component
     */
    public RawDummyComponent(final boolean injectSelf, final Class<?>... dependencies) {
        super();
        this.dependencies = Set.of(dependencies);
        this.injectSelf = injectSelf;
        this.loadMethod = provider -> {
        };
    }

    /**
     * Create a new dummy component.
     *
     * @param loadMethod   the method to call when loading this component
     * @param dependencies the dependencies of this component
     */
    public RawDummyComponent(final Consumer<DependencyProvider> loadMethod, final Class<?>... dependencies) {
        super();
        this.dependencies = Set.of(dependencies);
        this.injectSelf = false;
        this.loadMethod = loadMethod;
    }

    @Override
    public Set<Class<?>> requires() {
        return dependencies;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void load(final DependencyProvider dependencyProvider) {
        loadMethod.accept(dependencyProvider);
        if (injectSelf) {
            dependencyProvider.take(RawDummyComponent.class, this);
        }
        loaded = true;
    }
}
