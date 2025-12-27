package calculator.project.impl.coupling;

import calculator.project.index.ProjectIndex;
import calculator.project.ProjectMetricCalculator;
import context.ClassContext;
import lombok.AllArgsConstructor;

import java.util.Collection;

@AllArgsConstructor
public class NoccCalculator implements ProjectMetricCalculator {

    private final ProjectIndex projectIndex;

    @Override
    public void compute(Collection<ClassContext> contexts) {
        for (ClassContext childCtx : contexts) {
            for (String parent : childCtx.getDirectParents()) {
                projectIndex.find(parent)
                        .ifPresent(p -> p.getQualityMetrics().incrementNocc());
            }
        }
    }
}
