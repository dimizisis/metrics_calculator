package calculator.perclass.impl.complexity;

import calculator.perclass.ClassMetricCalculator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

public class MpcCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassContext ctx, QualityMetrics qm) {
        var methodsCalledCount = 0;
        for (MethodCallExpr methodCallExpr : ctx.getDecl().findAll(MethodCallExpr.class)) {
            try {
                String methodCallExprQualifiedSignature = methodCallExpr.resolve().getQualifiedSignature();
                String methodCallExprClass = methodCallExprQualifiedSignature.substring(0, methodCallExprQualifiedSignature.lastIndexOf("."));
                if (ctx.getBounds().contains(methodCallExprClass))
                    ++methodsCalledCount;
            } catch (Throwable ignored) {
            }
        }
        qm.setMpc(methodsCalledCount);
    }
}
