package calculator.single.impl.coupling;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

public class CboCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        double fanOut = classData.getDependencies().size();
        metrics.setCbo(fanOut);
        metrics.setDcc(fanOut);
    }
}
