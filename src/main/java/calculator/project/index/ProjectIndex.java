package calculator.project.index;

import infrastructure.entities.JavaClass;
import infrastructure.entities.JavaFile;

import java.util.*;
import java.util.stream.Collectors;

public final class ProjectIndex {
    private final Map<String, JavaClass> classesByQName;

    public ProjectIndex(Set<JavaFile> javaFiles) {
        classesByQName = javaFiles.stream()
                .flatMap(f -> f.getClasses().stream())
                .collect(Collectors.toMap(JavaClass::getQualifiedName, c -> c));
    }

    public Optional<JavaClass> find(String qname) {
        return Optional.ofNullable(classesByQName.get(qname));
    }

    public Collection<JavaClass> allClasses() {
        return classesByQName.values();
    }
}
