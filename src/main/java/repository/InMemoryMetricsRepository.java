package repository;

import context.ClassData;
import infrastructure.entities.JavaClass;
import infrastructure.metrics.QualityMetrics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * In-memory implementation of MetricsRepository backed by a map of JavaClass entities.
 */
public class InMemoryMetricsRepository implements MetricsRepository {
    private final Map<String, JavaClass> classMap;

    public InMemoryMetricsRepository(Collection<JavaClass> classes) {
        this.classMap = new HashMap<>();
        for (JavaClass javaClass : classes) {
            classMap.put(javaClass.getQualifiedName(), javaClass);
        }
    }

    @Override
    public Collection<String> getAllClassNames() {
        return classMap.keySet();
    }

    @Override
    public Collection<String> getProjectClassNames() {
        return classMap.values().stream()
            .filter(jc -> jc.getClassData() != null && jc.getClassData().isProjectClass())
            .map(JavaClass::getQualifiedName)
            .collect(Collectors.toList());
    }

    @Override
    public ClassData getClassData(String qualifiedName) {
        JavaClass javaClass = classMap.get(qualifiedName);
        return javaClass != null ? javaClass.getClassData() : null;
    }

    @Override
    public QualityMetrics getMetrics(String qualifiedName) {
        JavaClass javaClass = classMap.get(qualifiedName);
        return javaClass != null ? javaClass.getQualityMetrics() : null;
    }

    @Override
    public boolean isProjectClass(String qualifiedName) {
        ClassData classData = getClassData(qualifiedName);
        return classData != null && classData.isProjectClass();
    }
}