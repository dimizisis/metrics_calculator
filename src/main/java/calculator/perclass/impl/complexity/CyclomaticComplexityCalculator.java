package calculator.perclass.impl.complexity;

import calculator.perclass.ClassMetricCalculator;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SwitchExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import context.ClassContext;
import infrastructure.metrics.QualityMetrics;

public class CyclomaticComplexityCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassContext ctx, QualityMetrics qm) {
        double total = 0.0;
        int methodCount = 0;

        for (MethodDeclaration method : ctx.getDecl().getMethods()) {
            if (!method.isAbstract() && !method.isNative()) {
                int cc = countIfs(method) + countSwitch(method) + countSwitchExpressions(method) + 1;
                total += cc;
                methodCount++;
            }
        }

        // If class has no constructors, still count 1 path
        if (ctx.getDecl().getConstructors().isEmpty()) {
            methodCount++;
        }

        double avg = methodCount > 0 ? total / methodCount : -1.0;
        qm.setComplexity(avg);
    }

    private int countIfs(MethodDeclaration method) {
        return method.findAll(IfStmt.class).size();
    }

    private int countSwitch(MethodDeclaration method) {
        return method.findAll(SwitchStmt.class).stream()
                .mapToInt(s -> s.getEntries().size())
                .sum();
    }

    private int countSwitchExpressions(MethodDeclaration method) {
        return method.findAll(SwitchExpr.class).stream()
                .mapToInt(s -> s.getEntries().size())
                .sum();
    }
}
