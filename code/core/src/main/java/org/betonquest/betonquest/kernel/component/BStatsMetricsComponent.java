package org.betonquest.betonquest.kernel.component;

import org.betonquest.betonquest.api.BetonQuestApi;
import org.betonquest.betonquest.api.identifier.ReadableIdentifier;
import org.betonquest.betonquest.bstats.BStatsMetrics;
import org.betonquest.betonquest.bstats.InstructionMetricsSupplier;
import org.betonquest.betonquest.bstats.MetricsHolder;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The implementation of {@link AbstractCoreComponent} for {@link BStatsMetrics}.
 */
public class BStatsMetricsComponent extends AbstractCoreComponent {

    /**
     * The BStats metrics ID that is required and important for the bstats metrics to work properly.
     */
    private static final int BSTATS_METRICS_ID = 551;

    /**
     * The BStats metrics to load.
     */
    @Nullable
    private BStatsMetrics bStatsMetrics;

    /**
     * Create a new BStatsMetricsComponent.
     */
    public BStatsMetricsComponent() {
        super(true);
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(JavaPlugin.class, Compatibility.class, BetonQuestApi.class);
    }

    @Override
    public boolean isLoaded() {
        return bStatsMetrics != null;
    }

    @Override
    public void load(final DependencyProvider dependencyProvider) {
        final JavaPlugin plugin = getDependency(JavaPlugin.class);
        final Compatibility compatibility = getDependency(Compatibility.class);
        final BetonQuestApi betonQuestApi = getDependency(BetonQuestApi.class);

        final Map<String, InstructionMetricsSupplier<? extends ReadableIdentifier>> collectedMetrics = injectedDependencies.stream()
                .filter(injectedDependency -> MetricsHolder.class.isAssignableFrom(injectedDependency.type()))
                .map(injectedDependency -> (MetricsHolder) injectedDependency.dependency())
                .map(MetricsHolder::metricsSupplier)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        this.bStatsMetrics = new BStatsMetrics(plugin, new Metrics(plugin, BSTATS_METRICS_ID), collectedMetrics, compatibility, betonQuestApi.instructions());

        dependencyProvider.take(BStatsMetrics.class, bStatsMetrics);
    }
}
