package org.betonquest.betonquest.kernel;

import java.util.Set;

public interface CoreComponent {

    Set<Class<?>> requires();

    boolean requires(Class<?> type);

    <U> void inject(Class<U> dependencyClass, U component);

    boolean canLoad();

    boolean isLoaded();

    void load(DependencyProvider providerCallback);

    interface DependencyProvider {

        <U> void take(Class<U> type, U dependency);
    }
}
