package calculator.single.impl.coupling;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Data Abstraction Coupling (DAC) metric for a given class.
 * DAC is defined as the number of abstract data types (ADTs) used by the class.
 * </p>
 */
public class DacCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        metrics.setDac(classData.getDataAbstractionCoupling());
    }
}
