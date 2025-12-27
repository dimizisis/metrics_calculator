package visitors;

import analysis.AnalysisBounds;
import calculator.single.ClassMetricCalculator;
import com.github.javaparser.Position;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import context.ClassData;
import infrastructure.entities.JavaClass;
import infrastructure.entities.JavaFile;
import infrastructure.metrics.QualityMetrics;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import lombok.AllArgsConstructor;
import util.ResolutionUtils;

import java.util.*;

@AllArgsConstructor
public class ClassVisitor extends VoidVisitorAdapter<Void> {

    private final Set<JavaFile> javaFiles;
    private final AnalysisBounds bounds;
    private final String filePath;
    private final List<ClassMetricCalculator> calculators;

    @Override
    public void visit(ClassOrInterfaceDeclaration d, Void arg) {
        analyze(d);
    }

    @Override
    public void visit(EnumDeclaration d, Void arg) {
        analyze(d);
    }

    private void analyze(TypeDeclaration<?> node) {
        if (!belongsToThisFile()) {
            return;
        }

        Optional<String> qnOpt = ResolutionUtils.resolveClassName(node);
        if (qnOpt.isEmpty()) {
            return;
        }

        String qualifiedName = qnOpt.get();
        Optional<JavaClass> javaClassOptional = findJavaClass(qualifiedName);
        if (javaClassOptional.isEmpty()) {
            return;
        }

        JavaClass javaClass = javaClassOptional.get();

        // Build ClassData using builder pattern
        ClassData.Builder builder = ClassData.builder(qualifiedName)
                .isProjectClass(bounds.contains(qualifiedName));

        // Collect field names
        Set<String> fieldNames = new HashSet<>();
        for (FieldDeclaration f : node.getFields()) {
            f.getVariables().forEach(v -> {
                String fieldName = v.getNameAsString();
                builder.addFieldName(fieldName);
                fieldNames.add(fieldName);
            });
        }
        builder.fieldCount(node.getFields().size());

        // Collect dependencies, method-field access patterns, and method calls
        Set<String> dependencies = new HashSet<>();
        Set<String> methodsCalled = new HashSet<>();
        List<TreeSet<String>> methodFieldSets = new ArrayList<>();

        node.getMethods().forEach(m -> {
            builder.addMethod(m);

            // Track field access for this method
            TreeSet<String> methodFieldSet = new TreeSet<>();

            // Collect return type as dependency
            ResolutionUtils.resolveTypeName(m.getType())
                    .ifPresent(t -> addDependencyIfInBounds(dependencies, t, qualifiedName));

            // Collect parameter types as dependencies
            m.getParameters().forEach(p ->
                    ResolutionUtils.resolveTypeName(p.getType())
                            .ifPresent(t -> addDependencyIfInBounds(dependencies, t, qualifiedName))
            );

            // Collect exception types as dependencies
            try {
                m.resolve().getSpecifiedExceptions()
                        .forEach(ex -> addDependencyIfInBounds(dependencies, ex.describe(), qualifiedName));
            } catch (Exception ignored) { }

            // Track field accesses within method
            m.findAll(NameExpr.class).forEach(ne -> {
                if (fieldNames.contains(ne.getNameAsString())) {
                    methodFieldSet.add(ne.getNameAsString());
                }
            });

            methodFieldSets.add(methodFieldSet);

            // Collect method calls
            m.findAll(MethodCallExpr.class).forEach(call ->
                    ResolutionUtils.resolveMethod(call).ifPresent(r -> {
                        methodsCalled.add(r.getQualifiedSignature());
                        String calledClass = r.getPackageName() + "." + r.getClassName();
                        addDependencyIfInBounds(dependencies, calledClass, qualifiedName);
                    })
            );
        });

        // Add all dependencies and method calls to builder
        dependencies.forEach(builder::addDependency);
        methodsCalled.forEach(builder::addMethodCalled);
        methodFieldSets.forEach(builder::addMethodFieldSet);

        // Collect direct parents
        Set<String> directParents = new HashSet<>();
        if (node.isClassOrInterfaceDeclaration()) {
            node.asClassOrInterfaceDeclaration()
                    .getExtendedTypes()
                    .forEach(et ->
                            ResolutionUtils.resolveTypeName(et).ifPresent(parent -> {
                                directParents.add(parent);
                                addDependencyIfInBounds(dependencies, parent, qualifiedName);
                            })
                    );
        }
        directParents.forEach(builder::addDirectParent);

        // Pre-compute DIT (Depth of Inheritance Tree)
        int dit = computeDepthOfInheritance(node);
        builder.depthOfInheritance(dit);

        // Pre-compute MPC (Message Passing Coupling)
        int mpc = computeMessagePassingCoupling(node, qualifiedName);
        builder.messagePassingCoupling(mpc);

        // Pre-compute DAC (Data Abstraction Coupling)
        int dac = computeDataAbstractionCoupling(node, qualifiedName);
        builder.dataAbstractionCoupling(dac);

        // Pre-compute Size1 (total member lines)
        int totalMemberLines = computeTotalMemberLines(node);
        builder.totalMemberLines(totalMemberLines);

        // Pre-compute inner class count
        int innerClassCount = computeInnerClassCount(node);
        builder.innerClassCount(innerClassCount);

        // Build the ClassData
        ClassData classData = builder.build();
        javaClass.setClassData(classData);

        // Run all single-class metric calculators
        QualityMetrics qm = javaClass.getQualityMetrics();
        calculators.forEach(c -> c.compute(classData, qm));
    }

    private boolean belongsToThisFile() {
        return javaFiles.stream().anyMatch(f -> f.getPath().equals(filePath));
    }

    private Optional<JavaClass> findJavaClass(String qualifiedName) {
        return javaFiles.stream()
                .filter(f -> f.getPath().equals(filePath))
                .findFirst()
                .flatMap(f -> f.getClasses().stream()
                        .filter(c -> qualifiedName.equals(c.getQualifiedName()))
                        .findFirst());
    }

    private void addDependencyIfInBounds(Set<String> dependencies, String typeName, String selfQualifiedName) {
        if (typeName != null && !typeName.equals(selfQualifiedName) && bounds.contains(typeName)) {
            dependencies.add(typeName);
        }
    }

    private int computeDepthOfInheritance(TypeDeclaration<?> node) {
        try {
            return (int) node.resolve()
                    .getAllAncestors()
                    .stream()
                    .filter(ancestor -> bounds.contains(ancestor.getQualifiedName()))
                    .count();
        } catch (Throwable ignored) {
            return 0;
        }
    }

    private int computeMessagePassingCoupling(TypeDeclaration<?> node, String selfQualifiedName) {
        int count = 0;
        for (MethodCallExpr methodCallExpr : node.findAll(MethodCallExpr.class)) {
            try {
                String methodSignature = methodCallExpr.resolve().getQualifiedSignature();
                String methodClass = methodSignature.substring(0, methodSignature.lastIndexOf("."));
                if (!methodClass.equals(selfQualifiedName) && bounds.contains(methodClass)) {
                    count++;
                }
            } catch (Throwable ignored) {
            }
        }
        return count;
    }

    private int computeDataAbstractionCoupling(TypeDeclaration<?> node, String selfQualifiedName) {
        int count = 0;
        for (FieldDeclaration field : node.getFields()) {
            if (field.getElementType().isPrimitiveType()) {
                continue;
            }
            try {
                String typeName = field.getElementType().resolve().asReferenceType().getQualifiedName();
                if (!typeName.equals(selfQualifiedName) && bounds.contains(typeName)) {
                    count++;
                }
            } catch (Throwable ignored) {
            }
        }
        return count;
    }

    private int computeTotalMemberLines(TypeDeclaration<?> node) {
        int totalLines = 0;
        for (BodyDeclaration<?> member : node.getMembers()) {
            if (member.getBegin().isPresent() && member.getEnd().isPresent()) {
                Position begin = member.getBegin().get();
                Position end = member.getEnd().get();
                totalLines += Math.max(0, end.line - begin.line);
            }
        }
        return totalLines;
    }

    private int computeInnerClassCount(TypeDeclaration<?> node) {
        int count = 0;
        for (BodyDeclaration<?> member : node.getMembers()) {
            if (member.isClassOrInterfaceDeclaration()) {
                count++;
            }
        }
        return count;
    }

}
