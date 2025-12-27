package calculator.single.impl.size;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Size2 metric for a class, defined as the sum of the number of fields
 * and the number of methods in the class.
 */
public class Size2Calculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        int size2 = classData.getFieldCount() + classData.getMethods().size();
        metrics.setSize2(size2);
    }
}
