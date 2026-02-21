package org.betonquest.betonquest.kernel;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The default implementation of {@link CoreComponentLoader}.
 */
public class DefaultCoreComponentLoader implements CoreComponentLoader, CoreComponent.DependencyProvider {

    /**
     * Contains all registered components.
     */
    private final Set<CoreComponent> components;

    /**
     * Contains all initial injections.
     */
    private final Map<Class<?>, Object> initialInjections;

    /**
     * Create a new component loader.
     */
    public DefaultCoreComponentLoader() {
        this.components = new LinkedHashSet<>();
        this.initialInjections = new LinkedHashMap<>();
    }

    @Override
    public void register(final CoreComponent component) {
        this.components.add(component);
    }

    @Override
    public <T> void init(final Class<T> injectionClass, final T instance) {
        initialInjections.put(injectionClass, instance);
    }

    @Override
    public void load() {
        initialInject();
        do {
            checkForDependencyBlocking();
            components.stream().filter(CoreComponent::canLoad).forEach(component -> component.load(this));
        } while (components.stream().anyMatch(component -> !component.isLoaded()));
    }

    private void checkForDependencyBlocking() {
        if (components.stream().noneMatch(CoreComponent::canLoad)) {
            final String remainingComponents = components.stream()
                    .filter(coreComponent -> !coreComponent.canLoad())
                    .map(Object::getClass).map(Class::getSimpleName)
                    .collect(Collectors.joining(","));
            throw new IllegalStateException("Potential cyclomatic dependency while preparing components. Remaining components are missing dependencies: %s".formatted(remainingComponents));
        }
    }

    private void initialInject() {
        initialInjections.forEach((type, dependency) -> {
            injectToAll(type, type.cast(dependency));
        });
    }

    private <U> void injectToAll(final Class<U> injectionClass, final Object dependency) {
        components.forEach(comp -> comp.inject(injectionClass, injectionClass.cast(dependency)));
    }

    @Override
    public <U> void take(final Class<U> type, final U dependency) {
        components.forEach(dep -> dep.inject(type, dependency));
    }
}
