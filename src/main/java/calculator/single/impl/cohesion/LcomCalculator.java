package calculator.single.impl.cohesion;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

import java.util.TreeSet;

/**
 * Computes the LCOM (Lack of Cohesion of Methods) metric.
 * LCOM is calculated based on the number of method pairs that do not share
 * any instance variables minus the number of method pairs that do share
 * instance variables. If the result is negative, LCOM is set to zero.
 * If a class has no methods, LCOM is set to -1.
 */
public class LcomCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        var sets = classData.getMethodFieldSets();
        int n = sets.size();
        if (n == 0) {
            metrics.setLcom(-1.0);
            return;
        }

        double lcom = 0.0;
        for (int i = 0; i < n; ++i) {
            for (int j = i + 1; j < n; ++j) {
                TreeSet<String> a = new TreeSet<>(sets.get(i));
                var b = sets.get(j);
                if (!a.isEmpty() && !b.isEmpty()) {
                    a.retainAll(b);
                    lcom += a.isEmpty() ? 1 : -1;
                }
            }
        }
        metrics.setLcom(Math.max(lcom, 0.0));
    }
}
