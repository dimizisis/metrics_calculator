package infrastructure.entities;

import infrastructure.metrics.QualityMetrics;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
public class JavaFile {

    private String path;
    private Set<JavaClass> classes;
    private QualityMetrics qualityMetrics;

    public JavaFile(String path, Set<JavaClass> classes) {
        this.path = path;
        this.qualityMetrics = new QualityMetrics();
        this.classes = classes;
    }

    public JavaFile(String path, QualityMetrics qualityMetrics) {
        this.path = path;
        this.qualityMetrics = qualityMetrics;
    }

    public JavaFile(String path) {
        this.path = path;
        this.qualityMetrics = new QualityMetrics();
        this.classes = ConcurrentHashMap.newKeySet();
    }

    public void aggregateMetrics() {
        for (JavaClass aClass : getClasses()) {
            getQualityMetrics().add(aClass.getQualityMetrics());
        }
        setQMOODMetrics();
    }

    private void setQMOODMetrics() {
        QualityMetrics qm = getQualityMetrics();
        qm.setReusability(-0.25 * qm.getDcc() + 0.25 * qm.getCamc() + 0.5 * qm.getNpm() + 0.5 * qm.getDsc());
        qm.setFlexibility(-0.25 * qm.getDcc() + 0.25 * qm.getDam() + 0.5 * qm.getMoa() + 0.5 * qm.getNop());
        qm.setUnderstandability(-0.33 * qm.getAna() + 0.33 * qm.getDam() + 0.33 * qm.getCamc() - 0.33 * qm.getDcc() - 0.33 * qm.getNop() - 0.33 * qm.getNom() - 0.33 * qm.getDsc());
        qm.setFunctionality(0.12 * qm.getCamc() + 0.22 * qm.getNop() + 0.22 * qm.getNpm() + 0.22 * qm.getDsc() + 0.22 * qm.getNoh());
        qm.setExtendibility(0.5 * qm.getAna() - 0.5 * qm.getDcc() + 0.5 * qm.getMfa() + 0.5 * qm.getNop());
        qm.setEffectiveness(0.2 * qm.getAna() + 0.2 * qm.getDam() + 0.2 * qm.getMoa() + 0.2 * qm.getMfa() + 0.2 * qm.getNop());
    }


    public String getClassNames() {
        StringBuilder classesAsStringBuilder = new StringBuilder();
        String classesDelimiter = "/";
        for (JavaClass aClass : this.getClasses()) {
            classesAsStringBuilder.append(aClass.getQualifiedName()).append(classesDelimiter);
        }
        if (classesAsStringBuilder.lastIndexOf(classesDelimiter) != -1)
            classesAsStringBuilder.replace(classesAsStringBuilder.lastIndexOf(classesDelimiter), classesAsStringBuilder.lastIndexOf(classesDelimiter)+1, "");
        String classesAsString = classesAsStringBuilder.toString();
        return classesAsString.isEmpty() ? "" : classesAsString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JavaFile javaFile = (JavaFile) o;
        return Objects.equals(path, javaFile.path) && Objects.equals(qualityMetrics, javaFile.qualityMetrics);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, qualityMetrics);
    }

    @Override
    public String toString() {
        return this.getPath();
    }
}
