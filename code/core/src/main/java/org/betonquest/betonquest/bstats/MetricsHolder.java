package org.betonquest.betonquest.bstats;

import java.util.Map;

/**
 * The {@link MetricsHolder} identifies a class being considered for metrics published to {@link BStatsMetrics}.
 */
@FunctionalInterface
public interface MetricsHolder {

    /**
     * Returns the metrics for this holder.
     *
     * @return the map containing the metrics
     */
    Map.Entry<String, CompositeInstructionMetricsSupplier<?>> metricsSupplier();
}
