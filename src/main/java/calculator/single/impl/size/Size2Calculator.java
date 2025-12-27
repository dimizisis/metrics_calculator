package calculator.single.impl.size;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

public class Size2Calculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        int size2 = classData.getFieldCount() + classData.getMethods().size();
        metrics.setSize2(size2);
    }
}
