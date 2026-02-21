package org.betonquest.betonquest.kernel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default abstract implementation of {@link CoreComponent} aiming to reduce boilerplate code.
 *
 * @param <T> the type of the component
 */
public abstract class AbstractCoreComponent<T> implements CoreComponent<T> {

    /**
     * The type of the component.
     */
    private final Class<T> type;

    /**
     * The dependencies of the component.
     */
    private final Set<Class<?>> dependencies;

    /**
     * The injected dependencies of this component.
     */
    private final Map<Class<?>, Object> injectedDependencies;

    /**
     * Create a new component.
     *
     * @param type         the type of the component
     * @param dependencies the dependencies of the component
     */
    public AbstractCoreComponent(final Class<T> type, final Class<?>... dependencies) {
        this.type = type;
        this.dependencies = Arrays.stream(dependencies).collect(Collectors.toSet());
        this.injectedDependencies = new HashMap<>();
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public Set<Class<?>> dependencies() {
        return dependencies;
    }

    @Override
    public <U> void inject(final Class<U> dependencyClass, final U component) {
        this.injectedDependencies.put(dependencyClass, component);
    }

    /**
     * Get a dependency of this component.
     *
     * @param type the {@link Class} of the dependency to get
     * @param <U>  the type of the dependency
     * @return the dependency
     */
    protected <U> U getDependency(final Class<U> type) {
        return type.cast(injectedDependencies.get(type));
    }
}
