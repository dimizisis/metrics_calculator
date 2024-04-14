package infrastructure.entities;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
public class Project {
    private String clonePath;
    private Set<JavaFile> javaFiles;

    public Project(String clonePath, Set<JavaFile> javaFiles) {
        this.clonePath = clonePath;
        this.javaFiles = javaFiles;
    }

    public Project(String clonePath) {
        this.clonePath = clonePath;
        this.javaFiles = ConcurrentHashMap.newKeySet();
    }

}
