package org.betonquest.betonquest.kernel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The default implementation of {@link CoreComponentLoader}.
 */
public class DefaultCoreComponentLoader implements CoreComponentLoader {

    /**
     * Contains all registered components.
     */
    private final List<CoreComponent<?>> components;

    /**
     * Contains all initial injections.
     */
    private final Map<Class<?>, Object> initialInjections;

    /**
     * Create a new component loader.
     */
    public DefaultCoreComponentLoader() {
        this.components = new ArrayList<>();
        this.initialInjections = new HashMap<>();
    }

    @Override
    public void register(final CoreComponent<?> component) {
        this.components.add(component);
    }

    @Override
    public <T> void init(final Class<T> injectionClass, final T instance) {
        initialInjections.put(injectionClass, instance);
    }

    @Override
    public boolean isLoaded(final Class<?>... types) {
        final List<Class<?>> typeList = Arrays.asList(types);
        return components.stream().filter(comp -> typeList.contains(comp.type())).allMatch(CoreComponent::isLoaded);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> CoreComponent<T> component(final Class<T> type) {
        return components.stream().filter(comp -> type.isAssignableFrom(comp.type()))
                .map(comp -> (CoreComponent<T>) comp).findFirst().orElseThrow();
    }

    @Override
    public void load() {
        initialInject();
        final Set<Class<?>> loadedTypes = new HashSet<>();
        do {
            final Collection<Class<?>> freshlyLoaded = cycle(loadedTypes);
            loadedTypes.addAll(freshlyLoaded);
        } while (loadedTypes.size() != components.size());
    }

    private void initialInject() {
        initialInjections.forEach((type, dependency) -> {
            injectToAll(type, type.cast(dependency));
        });
    }

    private <U> void injectToAll(final Class<U> injectionClass, final Object dependency) {
        components.forEach(comp -> comp.inject(injectionClass, injectionClass.cast(dependency)));
    }

    private Collection<Class<?>> cycle(final Collection<Class<?>> loadedTypes) {
        final Set<CoreComponent<?>> toLoad = components.stream().filter(comp -> !comp.isLoaded())
                .filter(comp -> loadedTypes.containsAll(comp.dependencies()))
                .collect(Collectors.toSet());
        if (toLoad.isEmpty()) {
            final String remainingComponents = components.stream().map(CoreComponent::type).map(Class::getSimpleName).collect(Collectors.joining(","));
            throw new IllegalStateException("Cyclomatic dependency while preparing components. Remaining components: %s".formatted(remainingComponents));
        }
        toLoad.forEach(this::loadComponent);
        return toLoad.stream().map(CoreComponent::type).collect(Collectors.toSet());
    }

    private <T> void loadComponent(final CoreComponent<T> component) {
        final Set<CoreComponent<?>> dependent = components.stream()
                .filter(comp -> comp.dependencies().contains(component.type()))
                .collect(Collectors.toSet());
        component.load(injectToAll(dependent));
    }

    private CoreComponent.DependencyProvider injectToAll(final Collection<CoreComponent<?>> targets) {
        return new CoreComponent.DependencyProvider() {
            @Override
            public <U> void take(final Class<U> type, final U dependency) {
                targets.forEach(dep -> dep.inject(type, dependency));
            }
        };
    }
}
