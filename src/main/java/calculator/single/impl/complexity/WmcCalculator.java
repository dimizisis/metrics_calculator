package calculator.single.impl.complexity;

import calculator.single.ClassMetricCalculator;
import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

public class WmcCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassContext ctx, QualityMetrics qm) {
        var wmc = ctx
                .getMethods()
                .stream()
                .filter(methodDeclaration -> !methodDeclaration.isConstructorDeclaration()).count();
        qm.setWmc(wmc);
    }
}
