package calculator.single.impl.coupling;

import calculator.single.ClassMetricCalculator;
import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

public class CboCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassContext ctx, QualityMetrics qm) {
        double fanOut = ctx.getEfferent().size();
        qm.setCbo(fanOut);
        qm.setDcc(fanOut);
    }
}
