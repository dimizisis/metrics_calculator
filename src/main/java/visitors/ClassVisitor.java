package visitors;

import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.nodeTypes.modifiers.NodeWithAbstractModifier;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import infrastructure.entities.Class;
import infrastructure.entities.JavaFile;
import infrastructure.metrics.QualityMetrics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ClassVisitor extends VoidVisitorAdapter<Void> {

    private final Set<String> efferentCoupledClasses = ConcurrentHashMap.newKeySet();
    private final Set<String> afferentCoupledClasses = ConcurrentHashMap.newKeySet();
    private final List<TreeSet<String>> methodIntersection = new CopyOnWriteArrayList<>();
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
            Optional<JavaFile> jfOptional = javaFiles
                    .stream()
                    .filter(javaFile -> javaFile.getPath().equals(filePath))
                    .findAny();

            JavaFile jf = jfOptional.orElse(null);

            if (Objects.isNull(jf))
                return;

            if (javaClass.getFullyQualifiedName().isPresent()) {
                Optional<Class> currentClassObjectOptional = jf.getClasses().stream()
                        .filter(cl -> cl.getQualifiedName().equals(javaClass.getFullyQualifiedName().get()))
                        .findFirst();

                Class currentClassObject = currentClassObjectOptional.orElse(null);

                if (Objects.isNull(currentClassObject))
                    return;

                investigateExtendedTypes();
                visitAllClassMethods();

                try {
                    setClassMetrics(currentClassObject);
                } catch (Throwable ignored) {
                }
            }
        }
    }

    @Override
    public void visit(ClassOrInterfaceDeclaration javaClass, Void arg) {
        if (javaFiles.stream().anyMatch(javaFile -> javaFile.getPath().equals(filePath))) {
            Optional<JavaFile> jfOptional = javaFiles
                    .stream()
                    .filter(javaFile -> javaFile.getPath().equals(filePath))
                    .findAny();

            JavaFile jf = jfOptional.orElse(null);

            if (Objects.isNull(jf))
                return;

            if (javaClass.getFullyQualifiedName().isPresent()) {
                Optional<Class> currentClassObjectOptional = jf.getClasses().stream()
                        .filter(cl -> cl.getQualifiedName().equals(javaClass.getFullyQualifiedName().get()))
                        .findFirst();

                Class currentClassObject = currentClassObjectOptional.orElse(null);

                if (Objects.isNull(currentClassObject))
                    return;

                investigateExtendedTypes(); // Calculation of NOCC
                visitAllClassMethods();

                try {
                    setClassMetrics(currentClassObject);
                } catch(Throwable ignored) {
                }
            }
        }
    }

    private void setClassMetrics(Class currentClassObject) {
        currentClassObject.getQualityMetrics().setComplexity(calculateCC());
        currentClassObject.getQualityMetrics().setLCOM((double) calculateLCOM());
        currentClassObject.getQualityMetrics().setSIZE1(calculateSize1());
        currentClassObject.getQualityMetrics().setSIZE2(calculateSize2());
        currentClassObject.getQualityMetrics().setMPC(calculateMPC());
        currentClassObject.getQualityMetrics().setWMC(calculateWMC());
        currentClassObject.getQualityMetrics().setRFC(calculateRFC(currentClassObject.getQualityMetrics().getWMC()));
        currentClassObject.getQualityMetrics().setDAC(calculateDAC());
        currentClassObject.getQualityMetrics().setCBO((double) efferentCoupledClasses.size());
        currentClassObject.getQualityMetrics().setDIT(calculateDIT());
        currentClassObject.getQualityMetrics().setNOM(currentClassObject.getQualityMetrics().getWMC());
        currentClassObject.getQualityMetrics().setCAMC(calculateCAMC());
        currentClassObject.getQualityMetrics().setANA(calculateANA());
        currentClassObject.getQualityMetrics().setDCC((double) efferentCoupledClasses.size());
        currentClassObject.getQualityMetrics().setDSC(calculateDSC());
        currentClassObject.getQualityMetrics().setMFA(calculateMFA());
        currentClassObject.getQualityMetrics().setDAM(calculateDAM());
        currentClassObject.getQualityMetrics().setCIS(calculateCIS());
        currentClassObject.getQualityMetrics().setMOA(calculateMOA());
        currentClassObject.getQualityMetrics().setNPM(currentClassObject.getQualityMetrics().getCIS());
        currentClassObject.getQualityMetrics().setNOP(calculateNOP());
        currentClassObject.getQualityMetrics().setNOH(calculateNOH(currentClassObject.getQualityMetrics()));
    }

    /**
     * Calculate MPC metric value for the class we are referring to
     *
     * @return DIT metric value
     */
    private double calculateMPC() {
        for (MethodCallExpr methodCallExpr : javaClass.findAll(MethodCallExpr.class)) {
            try {
                String methodCallExprQualifiedName = methodCallExpr.resolve().getQualifiedName();
                String methodCallExprClass = methodCallExprQualifiedName.substring(0, methodCallExprQualifiedName.lastIndexOf("."));
                if (withinAnalysisBounds(methodCallExprClass))
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
    private int calculateDIT() {
        try {
            return (int) javaClass.resolve().getAllAncestors().stream().filter(ancestor -> withinAnalysisBounds(ancestor.getQualifiedName())).count();
        } catch (Throwable t) {
        }
        return 0;
    }

    /**
     * Calculate CC (Cyclomatic Complexity) metric value for
     * the class we are referring to
     *
     * @return CC metric value
     */
    private double calculateCC() {

        float total_ifs = 0.0f;
        int validClasses = 0;

        for (MethodDeclaration method : javaClass.getMethods()) {
            int ifs;
            if (!method.isAbstract() && !method.isNative()) {
                ifs = countIfs(method) + countSwitch(method) + 1;
                total_ifs += ifs;
                ++validClasses;
            }
        }
        if (javaClass.getConstructors().isEmpty())
            ++validClasses;

        return validClasses > 0 ? (total_ifs / validClasses) : -1;
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
    private int calculateDAC() {
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
                    if (intersection.isEmpty())
                        ++lcom;
                    else
                        --lcom;
                }
            }
        }
        return methodIntersection.isEmpty() ? -1 : Math.max(lcom, 0);
    }

    private double calculateWMC() {
        return javaClass.getMethods().stream().filter(methodDeclaration -> !methodDeclaration.isConstructorDeclaration()).count();
    }

    /**
     * Calculate DSC metric (number of classes contained in the class
     * we are referring to)
     *
     * @return the number of classes contained
     * in the class we are referring to
     */
    private int calculateDSC() {
        int classesNum = 1;
        for (BodyDeclaration<?> member : javaClass.getMembers()) {
            if (member.isClassOrInterfaceDeclaration()) {
                ++classesNum;
            }
        }
        return classesNum;
    }

    /**
     * Calculate MOA metric value for the class we are referring to
     *
     * @return MOA metric value
     */
    private int calculateMOA() {
        return (int) new HashSet<>(javaClass.getFields())
                .stream()
                .filter(field -> {
                    try {
                        return withinAnalysisBounds(field.getElementType().resolve().describe());
                    } catch (Throwable ignored) {
                        return false;
                    }
                }).count();
    }

    /**
     * Calculate CIS metric (number of public methods contained in the class
     * we are referring to)
     *
     * @return the number of public methods contained
     * in the class we are referring to
     */
    private int calculateCIS() {
        int publicMethods = 0;
        for (MethodDeclaration method : javaClass.getMethods())
            if (method.isPublic())
                ++publicMethods;
        return publicMethods;
    }

    /**
     * Calculate CAMC metric value for
     * the class we are referring to
     *
     * @return CAMC metric value
     */
    private double calculateCAMC() {
        List<MethodDeclaration> allMethods = javaClass.getMethods();
        int methodCount = allMethods.size();
        List<String> num;
        List<String> denum = new ArrayList<>();
        double numerator = 0;

        for (MethodDeclaration all_method : allMethods) {
            num = new ArrayList<>();
            List<Type> t = new ArrayList<>();
            for (Parameter p : all_method.getParameters())
                t.add(p.getType());

            for (Type type : t) {
                if (!num.contains(type.asString()))
                    num.add(type.asString());
                if (!denum.contains(type.asString()))
                    denum.add(type.asString());
            }
            numerator += num.size();

        }
        return (methodCount == 0 || denum.isEmpty()) ? -1 : numerator / (methodCount * denum.size());
    }

    /**
     * Calculate DAM metric value for the class we are referring to
     *
     * @return DAM metric value
     */
    private double calculateDAM() {
        double totalAttributes;
        double publicAttributes;
        List<FieldDeclaration> javaClassAttribute = new ArrayList<>(javaClass.getFields());
        totalAttributes = javaClassAttribute.size();
        publicAttributes = javaClassAttribute.stream().filter(a -> (!a.isProtected()) && (!a.isPrivate())).count();
        return totalAttributes == 0 ? -1 : ((totalAttributes - publicAttributes) / totalAttributes);
    }

    /**
     * Calculate MFA metric value for
     * the class we are referring to
     *
     * @return MFA metric value
     */
    private double calculateMFA() {
        List<ResolvedReferenceType> ancestors = new ArrayList<>();
        List<ResolvedReferenceType> superClasses;

        ClassOrInterfaceDeclaration javaClass = (ClassOrInterfaceDeclaration) this.javaClass;

        try {
            superClasses = javaClass
                    .getExtendedTypes()
                    .stream()
                    .map(extendedType -> {
                        try {
                            return extendedType.resolve().asReferenceType();
                        } catch (Throwable t) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        } catch (Throwable t) {
            return 0.0F;
        }

        superClasses
                .stream()
                .filter(superClass -> withinAnalysisBounds(superClass.getQualifiedName()))
                .forEach(ancestors::add);

        try {
            ancestors.addAll(getValidInterfaces(javaClass.resolve().getAllAncestors()));
        } catch (UnsolvedSymbolException ignored){}

        Set<ResolvedReferenceType> ancestorsSet = new HashSet<>();
        List<MethodDeclaration> javaClassMethods = new ArrayList<>(javaClass.getMethods());
        Set<ResolvedMethodDeclaration> ancestorMethods = new HashSet<>();

        for (int i = 0; i < ancestors.size(); ++i) {
            ResolvedReferenceType ancestor = ancestors.get(i);
            if (!ancestorsSet.contains(ancestor) && withinAnalysisBounds(ancestor.getQualifiedName())) {
                    ancestorsSet.add(ancestor);
                    ancestors.addAll(getValidInterfaces(ancestor));
                    try {
                        if (ancestor.getAllClassesAncestors().isEmpty())
                            break;
                        ResolvedReferenceType ancestorSuperClass = ancestor.getAllClassesAncestors().get(ancestor.getAllClassesAncestors().size() - 1);
                        if (withinAnalysisBounds(ancestorSuperClass.getQualifiedName())) {
                            ancestors.add(ancestorSuperClass);
                        }
                    } catch (UnsolvedSymbolException ignored) {
                    }
                    try {
                        ancestorMethods.addAll(ancestor.getAllMethods());
                    } catch (UnsolvedSymbolException ignored) {
                    }
                }

        }

        /* remove all javaClass methods from ancestors */
        try {
            ancestorMethods.forEach(ancestorMethod -> javaClassMethods.stream()
                    .filter(method -> ancestorMethod.getQualifiedSignature().equals(method.resolve().getQualifiedSignature()))
                    .forEach(method -> ancestorMethods.remove(ancestorMethod)));
        } catch (Throwable ignored) {
        }

        ancestorMethods.removeIf(method -> (method.accessSpecifier().equals(AccessSpecifier.PRIVATE)));
        javaClassMethods.removeIf(BodyDeclaration::isConstructorDeclaration);
        if (ancestorMethods.size() + javaClassMethods.size() == 0)
            return 0.0;

        return (double) ancestorMethods.size() / (ancestorMethods.size() + javaClassMethods.size());
    }

    /**
     * Calculate NOP metric value for
     * the class we are referring to
     *
     * @return NOP metric value
     */
    private int calculateNOP() {
        return (int) javaClass.getMethods()
                .stream()
                .filter(NodeWithAbstractModifier::isAbstract)
                .count();
    }

    private int calculateNOH(QualityMetrics currentQualityMetrics) {
        return (currentQualityMetrics.getNOCC() > 0 && currentQualityMetrics.getANA() == 0) ? 1 : 0;
    }

    /**
     * Calculate ANA metric value for the class we are referring to
     *
     * @return ANA metric value
     */
    private int calculateANA() {
        List<ResolvedReferenceType> ancestors = new ArrayList<>();
        Set<ResolvedReferenceType> ancestorsSet = new HashSet<>();
        try {
            if (withinAnalysisBounds(javaClass.resolve().getQualifiedName())) {
                ancestors.add(javaClass.resolve().getAncestors()
                        .get(javaClass.resolve().getAncestors().size() - 1));
                ancestors.addAll(getValidInterfaces(javaClass.resolve().getAllAncestors()));
            }
        } catch (Throwable ignored) {
        }

        for (int i = 0; i < ancestors.size(); ++i) {
            ResolvedReferenceType ancestor = ancestors.get(i);
            if (!ancestorsSet.contains(ancestor) && withinAnalysisBounds(ancestor.getQualifiedName())) {
                    ancestorsSet.add(ancestor);
                    try {
                        ancestors.addAll(getValidInterfaces(ancestor));
                    } catch (NullPointerException ignored) {
                    }
                    if (withinAnalysisBounds(ancestor.getQualifiedName())) {
                        try {
                            ancestors.add(ancestor.getAllClassesAncestors().get(ancestor.getAllClassesAncestors().size() - 1));
                        } catch (Throwable ignored) {
                        }
                    }
                }
        }
        return ancestorsSet.size();
    }

    /**
     * Get valid interfaces (ancestors) of class given
     *
     * @param javaClass the resolved class we are referring to
     * @return list of valid interfaces
     */
    private List<ResolvedReferenceType> getValidInterfaces(ResolvedReferenceType javaClass) {
        List<ResolvedReferenceType> ancestorsIf;
        try {
            ancestorsIf = javaClass.getAllInterfacesAncestors();
        } catch (UnsolvedSymbolException e) {
            return new ArrayList<>();
        }

        List<ResolvedReferenceType> validInterfaces = new ArrayList<>();
        for (ResolvedReferenceType resolvedReferenceType : ancestorsIf) {
            try {
                if (withinAnalysisBounds(resolvedReferenceType.getQualifiedName()))
                    validInterfaces.add(resolvedReferenceType);
            } catch (Throwable ignored) {
            }
        }
        return validInterfaces;
    }

    /**
     * Get valid interfaces (ancestors) of ancestors given
     *
     * @param ancestors list of ancestors
     * @return list of valid interfaces
     */
    private List<ResolvedReferenceType> getValidInterfaces(List<ResolvedReferenceType> ancestors) {
        ArrayList<ResolvedReferenceType> validInterfaces = new ArrayList<>();
        try {
            for (ResolvedReferenceType ancestor : ancestors) {
                for (int i = 0; i < ancestor.getAllInterfacesAncestors().size(); ++i) {
                    try {
                        if (withinAnalysisBounds(ancestor.getAllInterfacesAncestors().get(i).getQualifiedName()))
                            validInterfaces.add(ancestor.getAllInterfacesAncestors().get(i));
                    } catch (Throwable ignored) {
                    }
                }
            }
        } catch (Throwable t) {
        }
        return validInterfaces;
    }

    /**
     * Register field access
     *
     * @param fieldName the field we are referring to
     */
    private void registerFieldAccess(String fieldName) {
        registerCoupling(javaClass.resolve().getQualifiedName());
        methodIntersection.get(methodIntersection.size() - 1).add(fieldName);
    }

    /**
     * Register coupling of java class given
     *
     * @param className class name coupled with
     *                  the class we are referring to
     */
    private void registerCoupling(String className) {
        if (withinAnalysisBounds(className)) {
            registerEfferentCoupling(className);
            registerAfferentCoupling(className);
        }
    }

    /**
     * Register efferent coupling of java class given (FanOut)
     *
     * @param className class name coupled with
     *                  the class we are referring to
     */
    private void registerEfferentCoupling(String className) {
        efferentCoupledClasses.add(className);
    }

    /**
     * Register afferent coupling of java class given (FanIn)
     *
     * @param className class name coupled with
     *                  the class we are referring to
     */
    private void registerAfferentCoupling(String className) {
        try {
            Class classObject = findClassByQualifiedName(className);
            if (Objects.nonNull(classObject) && !afferentCoupledClasses.contains(className)) {
                classObject.getQualityMetrics().setFanIn(classObject.getQualityMetrics().getFanIn() + 1);
                afferentCoupledClasses.add(className);
            }
        } catch (Throwable ignored) {
        }
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
            Optional<JavaFile> jfOptional = javaFiles
                    .stream()
                    .filter(javaFile -> javaFile.getClasses().contains(new Class(classQualifiedName)))
                    .findFirst();

            JavaFile jf = jfOptional.orElse(null);

            if (Objects.isNull(jf))
                return null;

            Optional<Class> classOptional = jf.getClasses().stream().filter(cl -> cl.getQualifiedName().equals(classQualifiedName)).findFirst();

            return classOptional.orElse(null);
        } catch (Throwable ignored) {
        }
        return null;
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
