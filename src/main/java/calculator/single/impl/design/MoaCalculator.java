package calculator.single.impl.design;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Measure of Aggregation (MOA) metric.
 * MOA is the count of fields whose types are within the project analysis bounds
 * (i.e., user-defined types that are part of the analyzed codebase).
 */
public class MoaCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        int moa = classData.getProjectTypeFieldCount();
        metrics.setMoa(moa);
    }
}
