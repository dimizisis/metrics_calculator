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
    }

    public String getClassNames() {
        StringBuilder classesAsStringBuilder = new StringBuilder();
        for (Class aClass : this.getClasses()) {
            classesAsStringBuilder.append(aClass.getQualifiedName()).append(",");
        }
        String classesAsString = classesAsStringBuilder.toString();
        return classesAsString.isEmpty() ? "" : classesAsString.substring(0, classesAsString.length() -1);
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
