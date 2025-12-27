package calculator.single.impl.size;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

public class DscCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        int classesNum = 1 + classData.getInnerClassCount();
        metrics.setDsc(classesNum);
    }
}
