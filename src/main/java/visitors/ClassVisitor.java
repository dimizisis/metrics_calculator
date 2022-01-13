package visitors;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import infrastructure.entities.Class;
import infrastructure.entities.JavaFile;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClassVisitor extends VoidVisitorAdapter<Void> {

    private final Set<String> efferentCoupledClasses = ConcurrentHashMap.newKeySet();
    private final List<TreeSet<String>> methodIntersection = new CopyOnWriteArrayList<>();

    private final Set<String> responseSet = ConcurrentHashMap.newKeySet();
    private final Set<String> methodsCalled = ConcurrentHashMap.newKeySet();

    private final String filePath;
    private final TypeDeclaration<?> javaClass;

    private final Set<JavaFile> javaFiles;

    public ClassVisitor(Set<JavaFile> javaFiles, String filePath, ClassOrInterfaceDeclaration javaClass) {
        this.javaFiles = javaFiles;
        this.filePath = filePath;
        this.javaClass = javaClass;
    }

    public ClassVisitor(Set<JavaFile> javaFiles, String filePath, EnumDeclaration javaClass) {
        this.javaFiles = javaFiles;
        this.filePath = filePath;
        this.javaClass = javaClass;
    }

    @Override
    public void visit(EnumDeclaration javaClass, Void arg) {
        if (javaFiles.stream().anyMatch(javaFile -> javaFile.getPath().equals(filePath))) {
            JavaFile jf = javaFiles
                    .stream()
                    .filter(javaFile -> javaFile.getPath().equals(filePath)).findAny().get();

            if (javaClass.getFullyQualifiedName().isPresent()) {
                Class currentClassObject = jf.getClasses().stream().filter(cl -> cl.getQualifiedName().equals(javaClass.getFullyQualifiedName().get())).findFirst().get();

                investigateExtendedTypes();
                visitAllClassMethods();

                try {

                    currentClassObject.getQualityMetrics().setComplexity(calculateCC());
                    currentClassObject.getQualityMetrics().setLCOM((double) calculateLCOM());
                    currentClassObject.getQualityMetrics().setSIZE1(calculateSize1());
                    currentClassObject.getQualityMetrics().setSIZE2(calculateSize2());
                    currentClassObject.getQualityMetrics().setMPC(calculateMPC());
                    currentClassObject.getQualityMetrics().setWMC(calculateWmc());
                    currentClassObject.getQualityMetrics().setRFC(calculateRFC(currentClassObject.getQualityMetrics().getWMC()));
                    currentClassObject.getQualityMetrics().setDAC(calculateDac());
                    currentClassObject.getQualityMetrics().setCBO((double) efferentCoupledClasses.size());
                    currentClassObject.getQualityMetrics().setDIT(calculateDit());
                    currentClassObject.getQualityMetrics().setNOM(currentClassObject.getQualityMetrics().getWMC());

                } catch (Throwable ignored) {
                }
            }
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration javaClass, Void arg) {
        if (javaFiles.stream().anyMatch(javaFile -> javaFile.getPath().equals(filePath))) {
            JavaFile jf = javaFiles
                    .stream()
                    .filter(javaFile -> javaFile.getPath().equals(filePath)).findAny().get();

            if (javaClass.getFullyQualifiedName().isPresent()) {
                Class currentClassObject = jf.getClasses().stream().filter(cl -> cl.getQualifiedName().equals(javaClass.getFullyQualifiedName().get())).findFirst().get();

                investigateExtendedTypes();
                visitAllClassMethods();

                try {

                    currentClassObject.getQualityMetrics().setComplexity(calculateCC());
                    currentClassObject.getQualityMetrics().setLCOM((double) calculateLCOM());
                    currentClassObject.getQualityMetrics().setSIZE1(calculateSize1());
                    currentClassObject.getQualityMetrics().setSIZE2(calculateSize2());
                    currentClassObject.getQualityMetrics().setMPC(calculateMPC());
                    currentClassObject.getQualityMetrics().setWMC(calculateWmc());
                    currentClassObject.getQualityMetrics().setRFC(calculateRFC(currentClassObject.getQualityMetrics().getWMC()));
                    currentClassObject.getQualityMetrics().setDAC(calculateDac());
                    currentClassObject.getQualityMetrics().setCBO((double) efferentCoupledClasses.size());
                    currentClassObject.getQualityMetrics().setDIT(calculateDit());
                    currentClassObject.getQualityMetrics().setNOM(currentClassObject.getQualityMetrics().getWMC());

                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

    /**
     * Calculate MPC metric value for the class we are referring to
     *
     * @return DIT metric value
     */
    private double calculateMPC() {
        for (MethodCallExpr methodCallExpr : javaClass.findAll(MethodCallExpr.class)) {
            try {
                methodsCalled.add(methodCallExpr.resolve().getQualifiedName());
            } catch (Throwable ignored) {
            }
        }
        return methodsCalled.size();
    }

    /**
     * Calculate RFC metric value for the class we are referring to
     *
     * @return DIT metric value
     */
    private double calculateRFC(double wmc) {
        return wmc + methodsCalled.size();
    }

    /**
     * Visit all class methods & register metrics values
     */
    private void visitAllClassMethods() {
        javaClass.getMethods()
                .forEach(this::visitMethod);
    }

    /**
     * Calculate DIT metric value for the class we are referring to
     *
     * @return DIT metric value
     */
    private int calculateDit() {
        try {
            return javaClass.resolve().getAllAncestors().size();
        } catch (Throwable t) {
            return 0;
        }
    }

    /**
     * Calculate CC (Cyclomatic Complexity) metric value for
     * the class we are referring to
     *
     * @return CC metric value
     */
    private double calculateCC() {

        float total_ifs = 0.0f;
        int valid_classes = 0;

        for (MethodDeclaration method : javaClass.getMethods()) {
            int ifs;
            if (!method.isAbstract() && !method.isNative()) {
                ifs = countIfs(method) + countSwitch(method) + 1;
                total_ifs += ifs;
                ++valid_classes;
            }
        }
        if (javaClass.getConstructors().size() == 0)
            ++valid_classes;

        return valid_classes > 0 ? (total_ifs / valid_classes) : -1;
    }

    /**
     * Count how many switch statements there are within a method
     *
     * @param method the method we are referring to
     * @return switch count
     */
    private int countSwitch(MethodDeclaration method) {
        final int[] count = {0};
        method.findAll(SwitchStmt.class).forEach(switchStmt -> count[0] += switchStmt.getEntries().size());
        return count[0];
    }

    /**
     * Count how many if statements there are within a method
     *
     * @param method the method we are referring to
     * @return if count
     */
    private int countIfs(MethodDeclaration method) {
        return method.findAll(IfStmt.class).size();
    }

    /**
     * Calculate Size1 (LOC) metric value for
     * the class we are referring to
     *
     * @return Size1 metric value
     */
    private int calculateSize1() {
        int size = 0;
        for (BodyDeclaration<?> member : javaClass.getMembers())
            if (member.getBegin().isPresent() && member.getEnd().isPresent())
                size += member.getEnd().get().line - member.getBegin().get().line;
        return size;
    }

    /**
     * Calculate Size2 (Fields + Methods size) metric value for
     * the class we are referring to
     *
     * @return Size2 metric value
     */
    private int calculateSize2() {
        return javaClass.getFields().size() + javaClass.getMethods().size();
    }

    /**
     * Calculate DAC metric value for
     * the class we are referring to
     *
     * @return DAC metric value
     */
    private int calculateDac() {
        int dac = 0;
        for (FieldDeclaration field : javaClass.getFields()) {
            if (field.getElementType().isPrimitiveType())
                continue;
            String typeName;
            try {
                typeName = field.getElementType().resolve().asReferenceType().getQualifiedName();
            } catch (Throwable t) {
                continue;
            }
            if (withinAnalysisBounds(typeName)) {
                ++dac;
            }
        }
        return dac;
    }

    /**
     * Calculate LCOM metric value for
     * the class we are referring to
     *
     * @return LCOM metric value
     */
    private int calculateLCOM() {
        javaClass.getMethods().forEach(methodDeclaration -> methodIntersection.add(new TreeSet<>()));
        int lcom = 0;
        for (int i = 0; i < methodIntersection.size(); ++i) {
            for (int j = i + 1; j < methodIntersection.size(); ++j) {
                AbstractSet<String> intersection = (TreeSet<String>) (methodIntersection.get(i)).clone();
                if ((!intersection.isEmpty()) || (!methodIntersection.isEmpty())) {
                    intersection.retainAll(methodIntersection.get(j));
                    if (intersection.size() == 0)
                        ++lcom;
                    else
                        --lcom;
                }
            }
        }
        return methodIntersection.size() == 0 ? -1 : Math.max(lcom, 0);
    }

    private double calculateWmc() {
        return javaClass.getMethods().stream().filter(methodDeclaration -> !methodDeclaration.isConstructorDeclaration()).count();
    }

    /**
     * Register field access
     *
     * @param fieldName the field we are referring to
     */
    private void registerFieldAccess(String fieldName) {
        registerCoupling(javaClass.resolve().getQualifiedName());
        this.methodIntersection.get(this.methodIntersection.size() - 1).add(fieldName);
    }

    /**
     * Register coupling of java class given
     *
     * @param className class name coupled with
     *                  the class we are referring to
     */
    private void registerCoupling(String className) {
        efferentCoupledClasses.add(className);
    }

    /**
     * Register extended types for the class we are referring to
     */
    private void investigateExtendedTypes() {
        for (ClassOrInterfaceType extendedType : ((ClassOrInterfaceDeclaration) javaClass).getExtendedTypes()) {
            String extendedTypeQualifiedName;
            try {
                extendedTypeQualifiedName = extendedType.resolve().asReferenceType().getQualifiedName();
            } catch (Throwable ignored) {
                return;
            }
            registerCoupling(extendedTypeQualifiedName);
            Class extendedClassObject = findClassByQualifiedName(extendedTypeQualifiedName);
            if (Objects.nonNull(extendedClassObject))
                extendedClassObject.getQualityMetrics().setNOCC(extendedClassObject.getQualityMetrics().getNOCC() + 1);
        }
    }

    private Class findClassByQualifiedName(String classQualifiedName) {
        try {
            JavaFile jf = javaFiles
                    .stream()
                    .filter(javaFile -> javaFile.getClasses().contains(new Class(classQualifiedName)))
                    .findFirst().get();
            return jf.getClasses().stream().filter(cl -> cl.getQualifiedName().equals(classQualifiedName)).findFirst().get();
        } catch (Throwable ignored) {
            return null;
        }
    }

    /**
     * Register field access of method given
     *
     * @param method the method we are referring to
     */
    private void investigateFieldAccess(MethodDeclaration method) {
        try {
            method.findAll(NameExpr.class).forEach(expr -> javaClass.getFields().forEach(classField -> classField.getVariables()
                    .stream().filter(var -> var.getNameAsString().equals(expr.getNameAsString()))
                    .forEach(var -> registerFieldAccess(expr.getNameAsString()))));
        } catch (Throwable ignored) {
        }
    }

    /**
     * Register exception usage of method given
     *
     * @param method the method we are referring to
     */
    private void investigateExceptions(MethodDeclaration method) {
        try {
            method.resolve().getSpecifiedExceptions()
                    .forEach(exception -> registerCoupling(exception.describe()));
        } catch (Throwable ignored) {
        }
    }

    /**
     * Register parameters of method given
     *
     * @param method the method we are referring to
     */
    private void investigateParameters(MethodDeclaration method) {
        try {
            method.getParameters()
                    .forEach(p -> {
                        try {
                            registerCoupling(p.getType().resolve().describe());
                        } catch (Throwable ignored) {
                        }
                    });
        } catch (Throwable ignored) {
        }
    }

    /**
     * Register invocation of method given
     *
     * @param method the method we are referring to
     */
    private void investigateInvocation(MethodDeclaration method) {
        try {
            method.findAll(MethodCallExpr.class)
                    .forEach(methodCall -> {
                        try {
                            registerMethodInvocation(methodCall.resolve().getPackageName() + "." + methodCall.resolve().getClassName(), methodCall.resolve().getQualifiedSignature());
                        } catch (Throwable ignored) {
                        }
                    });
        } catch (Throwable ignored) {
        }
    }

    /**
     * Register method invocation of class given
     *
     * @param className the name of the class we are referring to
     */
    private void registerMethodInvocation(String className, String signature) {
        registerCoupling(className);
        responseSet.add(signature);
        methodsCalled.add(signature);
    }

    /**
     * Visit the method given & register metrics values
     *
     * @param method the method of javaClass we are referring to
     */
    public void visitMethod(MethodDeclaration method) {

        try {
            registerCoupling(method.resolve().getReturnType().describe());
        } catch (Throwable ignored) {
        }

        responseSet.add(method.resolve().getQualifiedSignature());
        investigateExceptions(method);
        investigateParameters(method);
        investigateInvocation(method);
        investigateFieldAccess(method);
    }

    private boolean withinAnalysisBounds(String name) {
        for (JavaFile javaFile : javaFiles) {
           if (javaFile.getClasses().contains(new Class(name))) {
               return true;
           }
        }
        return false;
    }

}
