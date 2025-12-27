package calculator.single.impl.inheritance;

import calculator.single.ClassMetricCalculator;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Number of Polymorphic methods (NOP) metric.
 * NOP is defined as the count of abstract methods in a class.
 */
public class NopCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        long nop = classData.getMethods().stream()
                .filter(MethodDeclaration::isAbstract)
                .count();
        metrics.setNop((int) nop);
    }
}
