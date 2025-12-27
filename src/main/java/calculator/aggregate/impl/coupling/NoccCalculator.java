package calculator.aggregate.impl.coupling;

import calculator.aggregate.AggregateMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import repository.MetricsRepository;

public class NoccCalculator implements AggregateMetricCalculator {

    @Override
    public void compute(MetricsRepository repository) {
        for (String className : repository.getProjectClassNames()) {
            ClassData classData = repository.getClassData(className);

            for (String parent : classData.getDirectParents()) {
                if (repository.isProjectClass(parent)) {
                    QualityMetrics parentMetrics = repository.getMetrics(parent);
                    if (parentMetrics != null) {
                        parentMetrics.incrementNocc();
                    }
                }
            }
        }
    }
}
