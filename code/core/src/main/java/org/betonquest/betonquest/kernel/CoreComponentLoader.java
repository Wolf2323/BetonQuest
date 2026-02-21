package org.betonquest.betonquest.kernel;

import java.util.Optional;

public interface CoreComponentLoader {

    void register(CoreComponent component);

    <T> void init(Class<T> injectionClass, T instance);

    Optional<CoreComponent> component(Class<?> type);

    void load();
}
