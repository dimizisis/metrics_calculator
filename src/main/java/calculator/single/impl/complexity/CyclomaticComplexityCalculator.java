package calculator.single.impl.complexity;

import calculator.single.ClassMetricCalculator;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.SwitchExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

public class CyclomaticComplexityCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        double total = 0.0;
        int methodCount = 0;

        for (MethodDeclaration method : classData.getMethods()) {
            if (!method.isAbstract() && !method.isNative() && !method.isConstructorDeclaration()) {
                int cc = countIfs(method) + countSwitch(method) + countSwitchExpressions(method) + 1;
                total += cc;
                methodCount++;
            }
        }

        // Default to 1 if no methods
        if (methodCount == 0) {
            methodCount = 1;
        }

        double avg = total / methodCount;
        metrics.setComplexity(avg);
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
