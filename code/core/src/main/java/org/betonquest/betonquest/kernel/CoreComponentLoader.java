package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.api.QuestException;

public interface CoreComponentLoader {

    void register(CoreComponent component);

    <T> void init(Class<T> injectionClass, T instance);

    void load() throws QuestException;
}
