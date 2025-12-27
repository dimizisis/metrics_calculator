package calculator.impl.complexity;

import calculator.MetricCalculator;
import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

public class WmcCalculator implements MetricCalculator {
    @Override
    public void compute(ClassContext ctx, QualityMetrics qm) {
        var wmc = ctx
                .getMethods()
                .stream()
                .filter(methodDeclaration -> !methodDeclaration.isConstructorDeclaration()).count();
        qm.setWmc(wmc);
    }
}
