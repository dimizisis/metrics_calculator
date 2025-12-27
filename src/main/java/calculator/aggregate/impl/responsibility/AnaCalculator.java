package calculator.aggregate.impl.responsibility;

import calculator.aggregate.AggregateMetricCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import repository.MetricsRepository;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * Computes the Average Number of Ancestors (ANA) metric.
 * ANA represents the count of all ancestors (classes and interfaces) for each class
 * within the project analysis bounds.
 */
public class AnaCalculator implements AggregateMetricCalculator {
    @Override
    public void compute(MetricsRepository repository) {
        for (String className : repository.getProjectClassNames()) {
            ClassData classData = repository.getClassData(className);
            if (classData == null) continue;

            try {
                int ancestorsCount = countAncestors(classData, repository);
                QualityMetrics metrics = repository.getMetrics(className);
                if (metrics != null) {
                    metrics.setAna(ancestorsCount);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    private int countAncestors(ClassData classData, MetricsRepository repository) {
        Set<String> ancestorsSet = new HashSet<>();
        Deque<String> toVisit = new ArrayDeque<>(classData.getDirectParents());

        while (!toVisit.isEmpty()) {
            String ancestorName = toVisit.poll();

            // Skip if not a project class or already visited
            if (!repository.isProjectClass(ancestorName) || ancestorsSet.contains(ancestorName)) {
                continue;
            }

            ancestorsSet.add(ancestorName);

            // Get ancestor's parents and add them to visit queue
            ClassData ancestorData = repository.getClassData(ancestorName);
            if (ancestorData != null) {
                toVisit.addAll(ancestorData.getDirectParents());
            }
        }

        return ancestorsSet.size();
    }
}

