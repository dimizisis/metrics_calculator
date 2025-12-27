package calculator.single.impl.complexity;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

public class MpcCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        metrics.setMpc(classData.getMessagePassingCoupling());
    }
}
