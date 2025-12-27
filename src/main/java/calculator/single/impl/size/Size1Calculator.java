package calculator.single.impl.size;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Size1 metric, which represents the total number of lines of code
 * in a class, including all its members (aka LOC).
 */
public class Size1Calculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        metrics.setSize1(classData.getTotalMemberLines());
    }
}
