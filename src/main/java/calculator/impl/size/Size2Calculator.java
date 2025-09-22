package calculator.impl.size;

import calculator.MetricCalculator;
import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

public class Size2Calculator implements MetricCalculator {
    @Override
    public void compute(ClassContext ctx, QualityMetrics qm) {
        int size2 = ctx.getDecl().getFields().size() + ctx.getDecl().getMethods().size();
        qm.setSize2(size2);
    }
}
