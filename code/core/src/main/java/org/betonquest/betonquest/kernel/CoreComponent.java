package org.betonquest.betonquest.kernel;

import java.util.Set;

public interface CoreComponent {

    Set<Class<?>> requires();

    boolean requires(Class<?> type);

    void inject(LoadedDependency<?> loadedDependency);

    boolean canLoad();

    boolean isLoaded();

    void load(DependencyProvider providerCallback);
}
