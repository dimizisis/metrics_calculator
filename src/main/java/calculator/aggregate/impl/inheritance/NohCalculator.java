package calculator.aggregate.impl.inheritance;

import calculator.aggregate.AggregateMetricCalculator;
import infrastructure.metrics.QualityMetrics;
import repository.MetricsRepository;

/**
 * Computes the Number of Hierarchies (NOH) metric.
 * NOH = 1 if the class has children (NOCC > 0) but no ancestors (ANA == 0), otherwise 0.
 * This identifies classes that are roots of inheritance hierarchies.
 *
 * Note: This calculator must run AFTER NoccCalculator and AnaCalculator.
 */
public class NohCalculator implements AggregateMetricCalculator {
    @Override
    public void compute(MetricsRepository repository) {
        for (String className : repository.getProjectClassNames()) {
            QualityMetrics metrics = repository.getMetrics(className);
            if (metrics == null) continue;

            int noh = (metrics.getNocc() > 0 && metrics.getAna() == 0) ? 1 : 0;
            metrics.setNoh(noh);
        }
    }
}
