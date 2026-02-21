package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;

import java.util.Collection;
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
     * The logger to use for debugging and error messages.
     */
    private final BetonQuestLogger log;

    /**
     * Create a new component loader.
     *
     * @param log the logger to use
     */
    public DefaultCoreComponentLoader(final BetonQuestLogger log) {
        this.log = log;
        this.components = new LinkedHashSet<>();
        this.initialInjections = new LinkedHashSet<>();
        this.loaded = new LinkedHashSet<>();
    }

    @Override
    public void register(final CoreComponent component) {
        this.components.add(component);
    }

    @Override
    public <T> void init(final Class<T> type, final T instance) {
        initialInjections.add(new LoadedDependency<>(type, instance));
    }

    /**
     * Get a loaded instance by its type.
     * Will ignore multiple instances of the same type and just return the first one to find.
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

    /**
     * Get all loaded instances matching a given type.
     *
     * @param type the type of the instances to get
     * @param <T>  the type of the instances
     * @return a collection of loaded instances
     */
    public <T> Collection<T> getAll(final Class<T> type) {
        return loaded.stream()
                .filter(dependency -> dependency.match(type))
                .map(LoadedDependency::dependency)
                .map(type::cast)
                .collect(Collectors.toSet());
    }

    @Override
    public void load() {
        log.info("Loading BetonQuest %s components...".formatted(components.size()));
        initialInjections.forEach(this::injectToAll);
        log.debug("Injected initial %s dependencies into components.".formatted(initialInjections.size()));
        do {
            checkForDependencyBlocking();
            components.stream().filter(CoreComponent::canLoad).forEach(component -> component.load(this));
        } while (components.stream().anyMatch(component -> !component.isLoaded()));
        log.info("All %s components successfully loaded.".formatted(components.size()));
    }

    private void checkForDependencyBlocking() {
        if (components.stream().noneMatch(CoreComponent::canLoad)) {
            final String remainingComponents = components.stream()
                    .filter(coreComponent -> !coreComponent.canLoad() && !coreComponent.isLoaded())
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
