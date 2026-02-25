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
        if (component.isLoaded()) {
            throw new IllegalArgumentException("Cannot register already loaded component: %s - Use CoreComponentLoader.init() instead.".formatted(component.getClass().getSimpleName()));
        }
        this.components.add(component);
    }

    @Override
    public <T> void init(final Class<T> type, final T instance) {
        final LoadedDependency<T> newlyAddedDependency = new LoadedDependency<>(type, instance);
        if (initialInjections.stream().anyMatch(dependency -> dependency.match(type) || newlyAddedDependency.match(dependency.type()))) {
            final String existing = initialInjections.stream().filter(dependency -> dependency.match(type))
                    .map(LoadedDependency::type).map(Class::getSimpleName).sorted().collect(Collectors.joining(","));
            throw new IllegalStateException("Attempted to inject an instance for an already injected dependency: new %s ~ %s".formatted(type.getSimpleName(), existing));
        }
        initialInjections.add(newlyAddedDependency);
    }

    @Override
    public <T> T get(final Class<T> type) {
        final LoadedDependency<?> injectedDependency = loaded.stream()
                .filter(dependency -> dependency.match(type)).findFirst().orElseThrow();
        return type.cast(injectedDependency.dependency());
    }

    @Override
    public <T> Collection<T> getAll(final Class<T> type) {
        return loaded.stream()
                .filter(dependency -> dependency.match(type))
                .map(LoadedDependency::dependency)
                .map(type::cast)
                .collect(Collectors.toSet());
    }

    @Override
    public void load() {
        log.info("Loading %s BetonQuest components...".formatted(components.size()));
        initialInjections.forEach(this::injectToAll);
        log.debug("Injected initial %s dependencies into components.".formatted(initialInjections.size()));
        if (components.stream().anyMatch(CoreComponent::isLoaded)) {
            final String loaded = components.stream().filter(CoreComponent::isLoaded)
                    .map(CoreComponent::getClass).map(Class::getSimpleName).sorted().collect(Collectors.joining(","));
            components.removeIf(CoreComponent::isLoaded);
            log.warn("Components '%s' were already loaded. This might lead to unexpected behavior.".formatted(loaded));
        }
        if (components.isEmpty()) {
            log.warn("No components were registered. Skipping loading.");
            return;
        }
        do {
            checkForDependencyBlocking();
            components.stream().filter(CoreComponent::canLoad).forEach(component -> {
                component.loadComponent(this);
                log.debug("Loaded component '%s'.".formatted(component.getClass().getSimpleName()));
            });
        } while (components.stream().anyMatch(component -> !component.isLoaded()));
        log.info("All %s components successfully loaded.".formatted(components.size()));
    }

    private void checkForDependencyBlocking() {
        if (components.stream().noneMatch(CoreComponent::canLoad)) {
            final String remainingComponents = components.stream()
                    .filter(coreComponent -> !coreComponent.canLoad() && !coreComponent.isLoaded())
                    .map(Object::getClass)
                    .map(Class::getSimpleName)
                    .sorted()
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
        if (loaded.stream().anyMatch(known -> known.type().isAssignableFrom(type) || type.isAssignableFrom(known.type()))) {
            throw new IllegalStateException("Attempted to load a dependency compatible with another. This might cause non-deterministic behaviour and is therefore prevented: new %s ~ %s".formatted(type.getSimpleName(), dependency.getClass().getSimpleName()));
        }
        final LoadedDependency<U> loadedDependency = new LoadedDependency<>(type, dependency);
        components.forEach(component -> component.inject(loadedDependency));
        loaded.add(loadedDependency);
    }
}
