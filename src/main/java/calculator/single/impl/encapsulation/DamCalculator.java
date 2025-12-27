package calculator.single.impl.encapsulation;

import calculator.single.ClassMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;

/**
 * Computes the Data Access Metric (DAM).
 * DAM is the ratio of private/protected fields to total fields.
 * Formula: DAM = (private + protected fields) / total fields
 * Returns -1 if there are no fields.
 */
public class DamCalculator implements ClassMetricCalculator {
    @Override
    public void compute(ClassData classData, QualityMetrics metrics) {
        int totalFields = classData.getFieldCount();

        if (totalFields == 0) {
            metrics.setDam(-1.0);
            return;
        }

        int privateProtectedFields = classData.getPrivateProtectedFieldCount();
        double dam = (double) privateProtectedFields / totalFields;
        metrics.setDam(dam);
    }
}
