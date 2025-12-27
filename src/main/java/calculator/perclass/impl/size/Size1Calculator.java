package calculator.perclass.impl.size;

import calculator.perclass.ClassMetricCalculator;
import com.github.javaparser.Position;
import com.github.javaparser.ast.body.BodyDeclaration;
import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

public class Size1Calculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassContext ctx, QualityMetrics qm) {
        int size = 0;
        for (BodyDeclaration<?> m : ctx.getDecl().getMembers()) {
            if (m.getBegin().isPresent() && m.getEnd().isPresent()) {
                Position b = m.getBegin().get();
                Position e = m.getEnd().get();
                size += Math.max(0, e.line - b.line);
            }
        }
        qm.setSize1(size);
    }
}
