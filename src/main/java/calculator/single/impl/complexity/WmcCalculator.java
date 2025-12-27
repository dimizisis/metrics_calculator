package calculator.single.impl.complexity;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Weighted Methods per Class (WMC) metric for a class.
 * WMC is defined as the total number of methods in the class, excluding constructors.
 * Also sets NOM (Number of Methods) as it is an alias for WMC.
 */
public class WmcCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        var wmc = classData
                .getMethods()
                .stream()
                .filter(methodDeclaration -> !methodDeclaration.isConstructorDeclaration()).count();
        metrics.setWmc(wmc);
        metrics.setNom(wmc); // NOM is alias for WMC
    }
}
