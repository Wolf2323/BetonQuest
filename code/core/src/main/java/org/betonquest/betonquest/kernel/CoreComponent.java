package org.betonquest.betonquest.kernel;

import java.util.Set;

public interface CoreComponent<T> {

    Class<T> type();

    Set<Class<?>> dependencies();

    <U> void inject(Class<U> dependencyClass, U component);

    boolean isLoaded();

    void load(DependencyProvider providerCallback);

    interface DependencyProvider {

        <U> void take(Class<U> type, U dependency);
    }
}
