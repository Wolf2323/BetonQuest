package org.betonquest.betonquest.api.service;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.BetonQuestApiService;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The default implementation of the {@link BetonQuestApiService}.
 */
public class DefaultBetonQuestApiService implements BetonQuestApiService {

    /**
     * The {@link Supplier} for the {@link BetonQuestApi}.
     */
    private final Function<Plugin, BetonQuestApi> apiSupplier;

    /**
     * The cached {@link BetonQuestApi} instances for each {@link Plugin}.
     */
    private final Map<Plugin, BetonQuestApi> cachedApiInstances;

    /**
     * Creates a new instance of the {@link DefaultBetonQuestApiService}.
     *
     * @param apiSupplier the {@link Supplier} for the {@link BetonQuestApi}.
     */
    public DefaultBetonQuestApiService(final Function<Plugin, BetonQuestApi> apiSupplier) {
        this.apiSupplier = apiSupplier;
        this.cachedApiInstances = new HashMap<>();
    }

    @Override
    public BetonQuestApi api(final Plugin plugin) {
        return cachedApiInstances.computeIfAbsent(plugin, apiSupplier);
    }
}
