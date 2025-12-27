package infrastructure.entities;

import infrastructure.metrics.QualityMetrics;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Set;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
public class JavaFile {
    @ToString.Include
    private final String path;
    @EqualsAndHashCode.Exclude
    private final Set<JavaClass> classes;
    private final QualityMetrics qualityMetrics = new QualityMetrics();

    public void aggregateMetrics() {
        for (JavaClass aClass : classes) {
            qualityMetrics.add(aClass.getQualityMetrics());
        }
        computeQMOOD();
    }

    private void computeQMOOD() {
        qualityMetrics.setReusability(-0.25 * qualityMetrics.getDcc() + 0.25 * qualityMetrics.getCamc() + 0.5 * qualityMetrics.getNpm() + 0.5 * qualityMetrics.getDsc());
        qualityMetrics.setFlexibility(-0.25 * qualityMetrics.getDcc() + 0.25 * qualityMetrics.getDam() + 0.5 * qualityMetrics.getMoa() + 0.5 * qualityMetrics.getNop());
        qualityMetrics.setUnderstandability(-0.33 * qualityMetrics.getAna() + 0.33 * qualityMetrics.getDam() + 0.33 * qualityMetrics.getCamc() - 0.33 * qualityMetrics.getDcc() - 0.33 * qualityMetrics.getNop() - 0.33 * qualityMetrics.getNom() - 0.33 * qualityMetrics.getDsc());
        qualityMetrics.setFunctionality(0.12 * qualityMetrics.getCamc() + 0.22 * qualityMetrics.getNop() + 0.22 * qualityMetrics.getNpm() + 0.22 * qualityMetrics.getDsc() + 0.22 * qualityMetrics.getNoh());
        qualityMetrics.setExtendibility(0.5 * qualityMetrics.getAna() - 0.5 * qualityMetrics.getDcc() + 0.5 * qualityMetrics.getMfa() + 0.5 * qualityMetrics.getNop());
        qualityMetrics.setEffectiveness(0.2 * qualityMetrics.getAna() + 0.2 * qualityMetrics.getDam() + 0.2 * qualityMetrics.getMoa() + 0.2 * qualityMetrics.getMfa() + 0.2 * qualityMetrics.getNop());
    }

    public String getClassNames() {
        var classesAsStringBuilder = new StringBuilder();
        String classesDelimiter = "/";
        for (JavaClass aClass : classes) {
            classesAsStringBuilder.append(aClass.getQualifiedName()).append(classesDelimiter);
        }
        if (classesAsStringBuilder.lastIndexOf(classesDelimiter) != -1)
            classesAsStringBuilder.replace(classesAsStringBuilder.lastIndexOf(classesDelimiter), classesAsStringBuilder.lastIndexOf(classesDelimiter)+1, "");
        String classesAsString = classesAsStringBuilder.toString();
        return classesAsString.isEmpty() ? "" : classesAsString;
    }
}
