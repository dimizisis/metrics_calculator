package analysis;

import infrastructure.entities.JavaClass;
import infrastructure.entities.JavaFile;

import java.util.Set;
import java.util.stream.Collectors;

public class ProjectAnalysisBounds implements AnalysisBounds {

    private final Set<String> projectClasses;

    public ProjectAnalysisBounds(Set<JavaFile> javaFiles) {
        this.projectClasses = javaFiles.stream()
                .flatMap(f -> f.getClasses().stream())
                .map(JavaClass::getQualifiedName)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean contains(String qualifiedName) {
        return projectClasses.contains(qualifiedName);
    }
}
