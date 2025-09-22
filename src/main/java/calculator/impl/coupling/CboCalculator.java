package calculator.impl.coupling;

import calculator.MetricCalculator;
import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

public class CboCalculator implements MetricCalculator {
    @Override
    public void compute(ClassContext ctx, QualityMetrics qm) {
        double fanOut = ctx.getEfferent().size();
        qm.setCbo(fanOut);
        qm.setDcc(fanOut);
    }
}
