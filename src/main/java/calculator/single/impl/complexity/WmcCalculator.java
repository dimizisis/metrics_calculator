package calculator.single.impl.complexity;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Weighted Methods per Class (WMC) metric for a class.
 * WMC is defined as the total number of methods in the class, excluding constructors.
 */
public class WmcCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        var wmc = classData
                .getMethods()
                .stream()
                .filter(methodDeclaration -> !methodDeclaration.isConstructorDeclaration()).count();
        metrics.setWmc(wmc);
    }
}
