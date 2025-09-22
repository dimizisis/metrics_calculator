package context;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import lombok.Getter;

import java.util.*;

@Getter
public class ClassContext {
    private final TypeDeclaration<?> decl;
    private final List<MethodDeclaration> methods = new ArrayList<>();
    private final Set<String> ownerFieldNames = new HashSet<>();
    private final List<TreeSet<String>> methodFieldSets = new ArrayList<>();
    private final Set<String> methodsCalled = new HashSet<>();
    private final Set<String> efferent = new HashSet<>();

    public ClassContext(TypeDeclaration<?> decl) {
        this.decl = decl;
        for (FieldDeclaration f : decl.getFields()) {
            f.getVariables().forEach(v -> ownerFieldNames.add(v.getNameAsString()));
        }
    }

    public void startMethod(MethodDeclaration m) {
        methods.add(m);
        methodFieldSets.add(new TreeSet<>());
    }

    public void noteFieldAccess(String field) {
        methodFieldSets.getLast().add(field);
    }
}
