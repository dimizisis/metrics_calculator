package calculator;

import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

@FunctionalInterface
public interface MetricCalculator {
    /** Compute the metric and update the QualityMetrics object accordingly.
     *
     * @param ctx the context of the class being analyzed
     * @param qm  the QualityMetrics object to be updated
     */
    void compute(ClassContext ctx, QualityMetrics qm);
}
