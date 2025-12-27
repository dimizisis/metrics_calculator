package calculator.impl.coupling;

import calculator.MetricCalculator;
import com.github.javaparser.ast.body.FieldDeclaration;
import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

public class DacCalculator implements MetricCalculator {
    @Override
    public void compute(ClassContext ctx, QualityMetrics qm) {
        var dac = 0;
        for (FieldDeclaration field : ctx.getDecl().getFields()) {
            if (field.getElementType().isPrimitiveType())
                continue;
            String typeName;
            try {
                typeName = field.getElementType().resolve().asReferenceType().getQualifiedName();
            } catch (Throwable t) {
                continue;
            }
            if (ctx.getBounds().contains(typeName)) {
                ++dac;
            }
        }
        qm.setDac(dac);
    }
}
