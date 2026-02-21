package org.betonquest.betonquest.kernel;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The default implementation of {@link CoreComponentLoader}.
 */
public class DefaultCoreComponentLoader implements CoreComponentLoader, DependencyProvider {

    /**
     * Contains all registered components.
     */
    private final Set<CoreComponent> components;

    /**
     * Contains all initial injections.
     */
    private final Set<LoadedDependency<?>> initialInjections;

    /**
     * Contains all loaded components objects.
     */
    private final Set<LoadedDependency<?>> loaded;

    /**
     * Create a new component loader.
     */
    public DefaultCoreComponentLoader() {
        this.components = new LinkedHashSet<>();
        this.initialInjections = new LinkedHashSet<>();
        this.loaded = new LinkedHashSet<>();
    }

    @Override
    public void register(final CoreComponent component) {
        this.components.add(component);
    }

    @Override
    public <T> void init(final Class<T> injectionClass, final T instance) {
        initialInjections.add(new LoadedDependency<>(injectionClass, instance));
    }

    /**
     * Get a loaded instance by its type.
     *
     * @param type the type of the instance to get
     * @param <T>  the type of the instance
     * @return the loaded instance
     */
    public <T> T get(final Class<T> type) {
        final LoadedDependency<?> injectedDependency = loaded.stream()
                .filter(dependency -> dependency.match(type)).findFirst().orElseThrow();
        return type.cast(injectedDependency.dependency());
    }

    @Override
    public void load() {
        initialInjections.forEach(this::injectToAll);
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

    private void injectToAll(final LoadedDependency<?> loadedDependency) {
        components.forEach(component -> component.inject(loadedDependency));
        loaded.add(loadedDependency);
    }

    @Override
    public <U> void take(final Class<U> type, final U dependency) {
        final LoadedDependency<U> loadedDependency = new LoadedDependency<>(type, dependency);
        components.forEach(component -> component.inject(loadedDependency));
        loaded.add(loadedDependency);
    }
}
