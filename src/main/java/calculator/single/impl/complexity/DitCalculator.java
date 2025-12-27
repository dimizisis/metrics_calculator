package calculator.single.impl.complexity;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Depth of Inheritance Tree (DIT) metric for a class.
 * DIT is defined as the length of the longest path from the class to the root class in the inheritance hierarchy.
 */
public class DitCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        metrics.setDit(classData.getDepthOfInheritance());
    }
}
