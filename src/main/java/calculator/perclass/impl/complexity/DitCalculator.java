package calculator.perclass.impl.complexity;

import calculator.perclass.ClassMetricCalculator;
import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

public class DitCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassContext ctx, QualityMetrics qm) {
        int dit = 0;
        try {
            dit = (int) ctx.getDecl().resolve().getAllAncestors().stream().filter(ancestor -> ctx.getBounds().contains(ancestor.getQualifiedName())).count();
        } catch (Throwable ignored) {
        }
        qm.setDit(dit);
    }
}
