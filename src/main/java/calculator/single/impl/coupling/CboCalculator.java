package calculator.single.impl.coupling;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Coupling Between Object classes (CBO) metric.
 * CBO is defined as the number of classes to which a class is coupled (aka FanOut).
 * Also sets DCC (Direct Class Coupling) as it is an alias for CBO.
 */
public class CboCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        double fanOut = classData.getDependencies().size();
        metrics.setCbo(fanOut);
        metrics.setDcc(fanOut);
    }
}
