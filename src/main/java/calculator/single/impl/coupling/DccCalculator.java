package calculator.single.impl.coupling;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/** *
 * Computes the Direct Class Coupling (DCC) metric.
 * DCC measures the number of other classes to which a class is directly coupled (same as CBO).
 */
public class DccCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        double fanOut = classData.getDependencies().size();
        metrics.setDcc(fanOut);
    }
}
