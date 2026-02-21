package org.betonquest.betonquest.kernel;

public interface CoreComponentLoader {

    void register(CoreComponent<?> component);

    <T> void init(Class<T> injectionClass, T instance);

    boolean isLoaded(Class<?>... types);

    <T> CoreComponent<T> component(Class<T> type);

    void load();
}
