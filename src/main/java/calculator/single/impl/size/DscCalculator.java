package calculator.single.impl.size;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Design size in classes (DSC) metric for a class.
 * DSC is defined as the total number of nested classes within the class, including inner classes.
 */
public class DscCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        int classesNum = 1 + classData.getInnerClassCount();
        metrics.setDsc(classesNum);
    }
}
