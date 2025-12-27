package calculator.aggregate;

import repository.MetricsRepository;

/**
 * Calculator for metrics that require access to multiple classes in the project.
 * These calculators can query any class's data and metrics through the repository.
 *
 * Examples: NOCC (Number of Child Classes), project-wide coupling metrics
 */
@FunctionalInterface
public interface AggregateMetricCalculator {
    /**
     * Computes aggregate metrics by accessing and potentially updating
     * metrics across multiple classes in the repository.
     *
     * @param repository provides access to all class data and metrics
     */
    void compute(MetricsRepository repository);
}