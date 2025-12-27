package calculator.perclass.impl.size;

import calculator.perclass.ClassMetricCalculator;
import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

public class Size2Calculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassContext ctx, QualityMetrics qm) {
        int size2 = ctx.getDecl().getFields().size() + ctx.getDecl().getMethods().size();
        qm.setSize2(size2);
    }
}
