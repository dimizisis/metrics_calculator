package context;

import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.Getter;

import java.util.*;

/**
 * Immutable data class containing analyzed facts about a single class.
 * This holds all the information needed to compute metrics without
 * maintaining references to AST nodes or analysis configuration.
 */
@Getter
public class ClassData {
    private final String qualifiedName;
    private final boolean isProjectClass;
    private final List<MethodDeclaration> methods;
    private final Set<String> fieldNames;
    private final List<TreeSet<String>> methodFieldSets;
    private final Set<String> methodsCalled;
    private final Set<String> dependencies;
    private final Set<String> directParents;

    // Pre-computed metric values
    private final int depthOfInheritance;
    private final int messagePassingCoupling;
    private final int dataAbstractionCoupling;
    private final int fieldCount;
    private final int innerClassCount;
    private final int totalMemberLines;

    private ClassData(Builder builder) {
        this.qualifiedName = Objects.requireNonNull(builder.qualifiedName, "qualifiedName cannot be null");
        this.isProjectClass = builder.isProjectClass;
        this.methods = List.copyOf(builder.methods);
        this.fieldNames = Set.copyOf(builder.fieldNames);
        this.methodFieldSets = builder.methodFieldSets.stream()
                .map(TreeSet::new)
                .toList();
        this.methodsCalled = Set.copyOf(builder.methodsCalled);
        this.dependencies = Set.copyOf(builder.dependencies);
        this.directParents = Set.copyOf(builder.directParents);
        this.depthOfInheritance = builder.depthOfInheritance;
        this.messagePassingCoupling = builder.messagePassingCoupling;
        this.dataAbstractionCoupling = builder.dataAbstractionCoupling;
        this.fieldCount = builder.fieldCount;
        this.innerClassCount = builder.innerClassCount;
        this.totalMemberLines = builder.totalMemberLines;
    }

    public static Builder builder(String qualifiedName) {
        return new Builder(qualifiedName);
    }

    public static class Builder {
        private final String qualifiedName;
        private boolean isProjectClass = false;
        private final List<MethodDeclaration> methods = new ArrayList<>();
        private final Set<String> fieldNames = new HashSet<>();
        private final List<TreeSet<String>> methodFieldSets = new ArrayList<>();
        private final Set<String> methodsCalled = new HashSet<>();
        private final Set<String> dependencies = new HashSet<>();
        private final Set<String> directParents = new HashSet<>();
        private int depthOfInheritance = 0;
        private int messagePassingCoupling = 0;
        private int dataAbstractionCoupling = 0;
        private int fieldCount = 0;
        private int innerClassCount = 0;
        private int totalMemberLines = 0;

        private Builder(String qualifiedName) {
            this.qualifiedName = qualifiedName;
        }

        public Builder isProjectClass(boolean isProjectClass) {
            this.isProjectClass = isProjectClass;
            return this;
        }

        public Builder addMethod(MethodDeclaration method) {
            this.methods.add(method);
            return this;
        }

        public Builder addFieldName(String fieldName) {
            this.fieldNames.add(fieldName);
            return this;
        }

        public Builder addMethodFieldSet(TreeSet<String> fieldSet) {
            this.methodFieldSets.add(new TreeSet<>(fieldSet));
            return this;
        }

        public Builder addMethodCalled(String methodSignature) {
            this.methodsCalled.add(methodSignature);
            return this;
        }

        public Builder addDependency(String dependency) {
            this.dependencies.add(dependency);
            return this;
        }

        public Builder addDirectParent(String parent) {
            this.directParents.add(parent);
            return this;
        }

        public Builder addAllDependencies(Collection<String> dependencies) {
            this.dependencies.addAll(dependencies);
            return this;
        }

        public Builder addAllDirectParents(Collection<String> parents) {
            this.directParents.addAll(parents);
            return this;
        }

        public Builder depthOfInheritance(int depth) {
            this.depthOfInheritance = depth;
            return this;
        }

        public Builder messagePassingCoupling(int mpc) {
            this.messagePassingCoupling = mpc;
            return this;
        }

        public Builder dataAbstractionCoupling(int dac) {
            this.dataAbstractionCoupling = dac;
            return this;
        }

        public Builder fieldCount(int count) {
            this.fieldCount = count;
            return this;
        }

        public Builder innerClassCount(int count) {
            this.innerClassCount = count;
            return this;
        }

        public Builder totalMemberLines(int lines) {
            this.totalMemberLines = lines;
            return this;
        }

        public ClassData build() {
            return new ClassData(this);
        }
    }
}