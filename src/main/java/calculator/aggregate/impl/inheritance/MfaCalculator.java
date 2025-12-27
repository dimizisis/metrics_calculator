package calculator.aggregate.impl.inheritance;

import calculator.aggregate.AggregateMetricCalculator;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import repository.MetricsRepository;

import java.util.*;

/**
 * Computes the Measure of Functional Abstraction (MFA) metric.
 * MFA is the ratio of inherited methods to total methods.
 * Formula: MFA = inherited_methods / (inherited_methods + own_methods)
 *
 * Inherited methods are those defined in ancestor classes (excluding private methods
 * and methods that are overridden by the current class).
 */
public class MfaCalculator implements AggregateMetricCalculator {
    @Override
    public void compute(MetricsRepository repository) {
        for (String className : repository.getProjectClassNames()) {
            ClassData classData = repository.getClassData(className);
            if (classData == null) continue;

            try {
                double mfa = computeMfaForClass(classData, repository);
                QualityMetrics metrics = repository.getMetrics(className);
                if (metrics != null) {
                    metrics.setMfa(mfa);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private double computeMfaForClass(ClassData classData, MetricsRepository repository) {
        // Get own methods (excluding constructors)
        Set<String> ownMethodSignatures = new HashSet<>();
        long ownMethodCount = 0;
        for (MethodDeclaration method : classData.getMethods()) {
            if (!method.isConstructorDeclaration()) {
                ownMethodSignatures.add(getMethodSignature(method));
                ownMethodCount++;
            }
        }

        // Collect all inherited methods from ancestors
        Set<String> inheritedMethodSignatures = new HashSet<>();
        Set<String> visited = new HashSet<>();
        Deque<String> toVisit = new ArrayDeque<>(classData.getDirectParents());

        while (!toVisit.isEmpty()) {
            String ancestorName = toVisit.poll();

            // Skip if not a project class or already visited
            if (!repository.isProjectClass(ancestorName) || visited.contains(ancestorName)) {
                continue;
            }

            visited.add(ancestorName);
            ClassData ancestorData = repository.getClassData(ancestorName);
            if (ancestorData == null) continue;

            // Collect non-private methods from this ancestor
            for (MethodDeclaration method : ancestorData.getMethods()) {
                if (!method.isConstructorDeclaration() && !method.isPrivate()) {
                    String signature = getMethodSignature(method);
                    // Only add if not overridden by current class
                    if (!ownMethodSignatures.contains(signature)) {
                        inheritedMethodSignatures.add(signature);
                    }
                }
            }

            // Add ancestor's parents to visit queue
            toVisit.addAll(ancestorData.getDirectParents());
        }

        long inheritedMethodCount = inheritedMethodSignatures.size();
        long totalMethods = ownMethodCount + inheritedMethodCount;

        if (totalMethods == 0) {
            return 0.0;
        }

        return (double) inheritedMethodCount / totalMethods;
    }

    private String getMethodSignature(MethodDeclaration method) {
        StringBuilder signature = new StringBuilder();
        signature.append(method.getNameAsString());
        signature.append("(");
        signature.append(method.getParameters().stream()
                .map(p -> p.getType().asString())
                .reduce((a, b) -> a + "," + b)
                .orElse(""));
        signature.append(")");
        return signature.toString();
    }
}
