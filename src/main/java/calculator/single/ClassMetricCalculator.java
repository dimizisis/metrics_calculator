package calculator.single;

import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Calculator for metrics that can be computed using only a single class's data,
 * without needing to access other classes in the project.
 *
 * Examples: Cyclomatic Complexity, WMC, LCOM
 */
@FunctionalInterface
public interface ClassMetricCalculator {
    /**
     * Compute the metric and update the QualityMetrics object accordingly.
     *
     * @param classData immutable data about the class being analyzed
     * @param metrics the QualityMetrics object to be updated
     */
    void compute(ClassData classData, QualityMetrics metrics);
}
