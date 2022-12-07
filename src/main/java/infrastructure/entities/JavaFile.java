package infrastructure.entities;

import infrastructure.metrics.QualityMetrics;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JavaFile {

    private String path;
    private Set<Class> classes;
    private QualityMetrics qualityMetrics;

    public JavaFile(String path, Set<Class> classes) {
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
        for (Class aClass : getClasses()) {
            getQualityMetrics().add(aClass.getQualityMetrics());
        }
        setQMOODMetrics();
    }

    private void setQMOODMetrics() {
        getQualityMetrics().setReusability(-0.25 * getQualityMetrics().getDCC() + 0.25 * getQualityMetrics().getCAMC() + 0.5 * getQualityMetrics().getNPM() + 0.5 * getQualityMetrics().getDSC());
        getQualityMetrics().setFlexibility(-0.25 * getQualityMetrics().getDCC() + 0.25 * getQualityMetrics().getDAM() + 0.5 * getQualityMetrics().getMOA() + 0.5 * getQualityMetrics().getNOP());
        getQualityMetrics().setUnderstandability(-0.33 * getQualityMetrics().getANA() + 0.33 * getQualityMetrics().getDAM() + 0.33 * getQualityMetrics().getCAMC() - 0.33 * getQualityMetrics().getDCC() - 0.33 * getQualityMetrics().getNOP() - 0.33 * getQualityMetrics().getNOM() - 0.33 * getQualityMetrics().getDSC());
        getQualityMetrics().setFunctionality(0.12 * getQualityMetrics().getCAMC() + 0.22 * getQualityMetrics().getNOP() + 0.22 * getQualityMetrics().getNPM() + 0.22 * getQualityMetrics().getDSC() + 0.22 * getQualityMetrics().getNOH());
        getQualityMetrics().setExtendibility(0.5 * getQualityMetrics().getANA() - 0.5 * getQualityMetrics().getDCC() + 0.5 * getQualityMetrics().getMFA() + 0.5 * getQualityMetrics().getNOP());
        getQualityMetrics().setEffectiveness(0.2 * getQualityMetrics().getANA() + 0.2 * getQualityMetrics().getDAM() + 0.2 * getQualityMetrics().getMOA() + 0.2 * getQualityMetrics().getMFA() + 0.2 * getQualityMetrics().getNOP());
    }

    public String getClassNames() {
        StringBuilder classesAsStringBuilder = new StringBuilder();
        String classesDelimiter = "/";
        for (Class aClass : this.getClasses()) {
            classesAsStringBuilder.append(aClass.getQualifiedName()).append(classesDelimiter);
        }
        if (classesAsStringBuilder.lastIndexOf(classesDelimiter) != -1)
            classesAsStringBuilder.replace(classesAsStringBuilder.lastIndexOf(classesDelimiter), classesAsStringBuilder.lastIndexOf(classesDelimiter)+1, "");
        String classesAsString = classesAsStringBuilder.toString();
        return classesAsString.isEmpty() ? "" : classesAsString;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public QualityMetrics getQualityMetrics() {
        return qualityMetrics;
    }

    public void setQualityMetrics(QualityMetrics qualityMetrics) {
        this.qualityMetrics = qualityMetrics;
    }

    public Set<Class> getClasses() {
        return classes;
    }

    public void setClasses(Set<Class> classes) {
        this.classes = classes;
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
