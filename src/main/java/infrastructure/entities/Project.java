package infrastructure.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class Project {
    private final String clonePath;
    private final Set<JavaFile> javaFiles = new HashSet<>();
}
