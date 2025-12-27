package calculator.single.impl.encapsulation;

import calculator.single.ClassMetricCalculator;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Class Interface Size (CIS) metric.
 * CIS is defined as the number of public methods in a class.
 * Also sets NPM (Number of Public Methods) as it is an alias for CIS.
 */
public class CisCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        long cis = classData.getMethods().stream()
                .filter(MethodDeclaration::isPublic)
                .count();
        metrics.setCis((int) cis);
        metrics.setNpm((int) cis); // NPM is alias for CIS
    }
}
