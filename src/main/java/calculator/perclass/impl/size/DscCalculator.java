package calculator.perclass.impl.size;

import calculator.perclass.ClassMetricCalculator;
import com.github.javaparser.ast.body.BodyDeclaration;
import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

public class DscCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassContext ctx, QualityMetrics qm) {
        int classesNum = 1;
        for (BodyDeclaration<?> member : ctx.getDecl().getMembers()) {
            if (member.isClassOrInterfaceDeclaration()) {
                ++classesNum;
            }
        }
        qm.setDsc(classesNum);
    }
}
