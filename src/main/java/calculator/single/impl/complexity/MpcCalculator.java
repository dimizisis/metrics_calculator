package calculator.single.impl.complexity;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Message Passing Coupling (MPC) metric for a class.
 * MPC is defined as the number of method calls made by the class to methods of other classes.
 */
public class MpcCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        metrics.setMpc(classData.getMessagePassingCoupling());
    }
}
