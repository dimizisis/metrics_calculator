package calculator.single.impl.cohesion;

import calculator.single.ClassMetricCalculator;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Computes the Cohesion Among Methods of Class (CAMC) metric.
 * CAMC measures cohesion based on parameter type intersections across methods.
 * Formula: sum(distinct_parameter_types_per_method) / (method_count * total_distinct_parameter_types)
 * Returns -1 if there are no methods or no parameters.
 */
public class CamcCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        List<MethodDeclaration> methods = classData.getMethods();

        if (methods.isEmpty()) {
            metrics.setCamc(-1.0);
            return;
        }

        Set<String> allParamTypes = new HashSet<>();
        double numerator = methods.stream()
                .mapToDouble(method -> {
                    Set<String> methodParamTypes = method.getParameters().stream()
                            .map(param -> param.getType().asString())
                            .collect(Collectors.toSet());
                    allParamTypes.addAll(methodParamTypes);
                    return methodParamTypes.size();
                })
                .sum();

        if (allParamTypes.isEmpty()) {
            metrics.setCamc(-1.0);
            return;
        }

        double camc = numerator / (methods.size() * allParamTypes.size());
        metrics.setCamc(camc);
    }
}
