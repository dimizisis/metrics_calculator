package calculator.project;

import context.ClassContext;

import java.util.Collection;

@FunctionalInterface
public interface ProjectMetricCalculator {
    /** *
     * Computes project-level quality metrics based on the provided class contexts.
     *
     * @param contexts A collection of ClassContext objects representing the classes in the project.
     */
    void compute(Collection<ClassContext> contexts);
}