package calculator;

import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

@FunctionalInterface
public interface MetricCalculator {
    void compute(ClassContext ctx, QualityMetrics qm);
}
