package calculator.single.impl.coupling;

import calculator.single.ClassMetricCalculator;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Response For a Class (RFC) metric.
 * RFC is defined as WMC (number of methods) plus the number of external method calls.
 * Formula: RFC = WMC + |external method invocations|
 */
public class RfcCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        // Count methods excluding constructors (same as WMC)
        double wmc = classData.getMethods().stream()
                .filter(m -> !m.isConstructorDeclaration())
                .count();

        // Count external method calls
        double externalCalls = classData.getMethodsCalled().size();

        metrics.setRfc(wmc + externalCalls);
    }
}
