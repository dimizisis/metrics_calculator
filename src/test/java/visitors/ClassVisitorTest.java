package visitors;

import analysis.AnalysisBounds;
import calculator.single.ClassMetricCalculator;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import context.ClassData;
import infrastructure.entities.JavaClass;
import infrastructure.entities.JavaFile;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ClassVisitorTest {

    @BeforeEach
    void configureParser() {
        var solver = new CombinedTypeSolver();
        solver.add(new ReflectionTypeSolver());

        var symbolSolver = new JavaSymbolSolver(solver);
        var parserConfiguration = new ParserConfiguration()
                .setSymbolResolver(symbolSolver)
                .setAttributeComments(false)
                .setDetectOriginalLineSeparator(true);

        StaticJavaParser.setConfiguration(parserConfiguration);
    }

    @Test
    void testEmptyClass_CreatesBasicClassData() {
        // Arrange
        var code = "package test; public class EmptyClass {}";
        CompilationUnit cu = StaticJavaParser.parse(code);
        ClassOrInterfaceDeclaration classDecl = cu.getClassByName("EmptyClass").orElseThrow();

        var javaClass = new JavaClass("test.EmptyClass");
        var javaFile = new JavaFile("EmptyClass.java", Set.of(javaClass));
        Set<JavaFile> javaFiles = Set.of(javaFile);

        AnalysisBounds bounds = qname -> qname.startsWith("test.");
        List<ClassMetricCalculator> calculators = Collections.emptyList();

        var visitor = new ClassVisitor(javaFiles, bounds, "EmptyClass.java", calculators);

        // Act
        classDecl.accept(visitor, null);

        // Assert
        ClassData classData = javaClass.getClassData();
        assertNotNull(classData, "ClassData should be created");
        assertEquals("test.EmptyClass", classData.getQualifiedName());
        assertTrue(classData.isProjectClass());
        assertEquals(0, classData.getMethods().size());
        assertEquals(0, classData.getFieldNames().size());
        assertEquals(0, classData.getDependencies().size());
    }

    @Test
    void testClassWithMethods_CapturesMethods() {
        // Arrange
        String code = """
                package test;
                public class ClassWithMethods {
                    public void methodA() {
                        System.out.println("A");
                    }

                    public int methodB() {
                        return 42;
                    }

                    private String methodC() {
                        return "C";
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        ClassOrInterfaceDeclaration classDecl = cu.getClassByName("ClassWithMethods").orElseThrow();

        var javaClass = new JavaClass("test.ClassWithMethods");
        var javaFile = new JavaFile("ClassWithMethods.java", Set.of(javaClass));
        Set<JavaFile> javaFiles = Set.of(javaFile);

        AnalysisBounds bounds = qname -> qname.startsWith("test.");
        List<ClassMetricCalculator> calculators = Collections.emptyList();

        var visitor = new ClassVisitor(javaFiles, bounds, "ClassWithMethods.java", calculators);

        // Act
        classDecl.accept(visitor, null);

        // Assert
        ClassData classData = javaClass.getClassData();
        assertNotNull(classData);
        assertEquals(3, classData.getMethods().size(), "Should capture all 3 methods");
    }

    @Test
    void testClassWithFields_CapturesFieldNames() {
        // Arrange
        String code = """
                package test;
                public class ClassWithFields {
                    private int x;
                    private String name;
                    public double value;
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        ClassOrInterfaceDeclaration classDecl = cu.getClassByName("ClassWithFields").orElseThrow();

        var javaClass = new JavaClass("test.ClassWithFields");
        var javaFile = new JavaFile("ClassWithFields.java", Set.of(javaClass));
        Set<JavaFile> javaFiles = Set.of(javaFile);

        AnalysisBounds bounds = qname -> qname.startsWith("test.");
        List<ClassMetricCalculator> calculators = Collections.emptyList();

        var visitor = new ClassVisitor(javaFiles, bounds, "ClassWithFields.java", calculators);

        // Act
        classDecl.accept(visitor, null);

        // Assert
        ClassData classData = javaClass.getClassData();
        assertNotNull(classData);
        assertEquals(3, classData.getFieldNames().size(), "Should capture all 3 fields");
        assertTrue(classData.getFieldNames().contains("x"));
        assertTrue(classData.getFieldNames().contains("name"));
        assertTrue(classData.getFieldNames().contains("value"));
        assertEquals(3, classData.getFieldCount());
    }

    @Test
    void testClassWithInheritance_DoesNotCaptureDirectParentsIfNotWithinAnalysisBounds() {
        // Arrange
        String code = """
                package test;
                import java.util.ArrayList;
                public class ChildClass extends ArrayList<String> {
                    public void method() {}
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        ClassOrInterfaceDeclaration classDecl = cu.getClassByName("ChildClass").orElseThrow();

        var javaClass = new JavaClass("test.ChildClass");
        var javaFile = new JavaFile("ChildClass.java", Set.of(javaClass));
        Set<JavaFile> javaFiles = Set.of(javaFile);

        AnalysisBounds bounds = qname -> qname.startsWith("test.");
        List<ClassMetricCalculator> calculators = Collections.emptyList();

        var visitor = new ClassVisitor(javaFiles, bounds, "ChildClass.java", calculators);

        // Act
        classDecl.accept(visitor, null);

        // Assert
        ClassData classData = javaClass.getClassData();
        assertNotNull(classData);
        assertEquals(1, classData.getDirectParents().size());
        assertFalse(classData.getDirectParents().contains("java.util.ArrayList"));
    }

    @Test
    void testClassWithFieldAccess_CapturesMethodFieldSets() {
        // Arrange
        String code = """
                package test;
                public class ClassWithFieldAccess {
                    private int x;
                    private int y;

                    public void methodA() {
                        x = 10;
                        y = 20;
                    }

                    public void methodB() {
                        x = 5;
                    }

                    public void methodC() {
                        int z = 100;
                    }
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        ClassOrInterfaceDeclaration classDecl = cu.getClassByName("ClassWithFieldAccess").orElseThrow();

        var javaClass = new JavaClass("test.ClassWithFieldAccess");
        var javaFile = new JavaFile("ClassWithFieldAccess.java", Set.of(javaClass));
        Set<JavaFile> javaFiles = Set.of(javaFile);

        AnalysisBounds bounds = qname -> qname.startsWith("test.");
        List<ClassMetricCalculator> calculators = Collections.emptyList();

        var visitor = new ClassVisitor(javaFiles, bounds, "ClassWithFieldAccess.java", calculators);

        // Act
        classDecl.accept(visitor, null);

        // Assert
        ClassData classData = javaClass.getClassData();
        assertNotNull(classData);
        assertEquals(3, classData.getMethodFieldSets().size(), "Should have 3 field sets (one per method)");

        // methodA should access both x and y
        TreeSet<String> methodAFields = classData.getMethodFieldSets().getFirst();
        assertEquals(2, methodAFields.size());
        assertTrue(methodAFields.contains("x"));
        assertTrue(methodAFields.contains("y"));

        // methodB should access only x
        TreeSet<String> methodBFields = classData.getMethodFieldSets().get(1);
        assertEquals(1, methodBFields.size());
        assertTrue(methodBFields.contains("x"));

        // methodC should access no fields
        TreeSet<String> methodCFields = classData.getMethodFieldSets().get(2);
        assertEquals(0, methodCFields.size());
    }

    @Test
    void testClassWithInnerClass_CapturesInnerClassCount() {
        // Arrange
        String code = """
                package test;
                public class OuterClass {
                    private class InnerClass1 {}
                    private class InnerClass2 {}

                    public void method() {}
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        ClassOrInterfaceDeclaration classDecl = cu.getClassByName("OuterClass").orElseThrow();

        var javaClass = new JavaClass("test.OuterClass");
        var javaFile = new JavaFile("OuterClass.java", Set.of(javaClass));
        Set<JavaFile> javaFiles = Set.of(javaFile);

        AnalysisBounds bounds = qname -> qname.startsWith("test.");
        List<ClassMetricCalculator> calculators = Collections.emptyList();

        var visitor = new ClassVisitor(javaFiles, bounds, "OuterClass.java", calculators);

        // Act
        classDecl.accept(visitor, null);

        // Assert
        ClassData classData = javaClass.getClassData();
        assertNotNull(classData);
        assertEquals(2, classData.getInnerClassCount(), "Should count 2 inner classes");
    }

    @Test
    void testCalculatorsAreInvoked() {
        // Arrange
        String code = """
                package test;
                public class TestClass {
                    public void method() {}
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        ClassOrInterfaceDeclaration classDecl = cu.getClassByName("TestClass").orElseThrow();

        var javaClass = new JavaClass("test.TestClass");
        var javaFile = new JavaFile("TestClass.java", Set.of(javaClass));
        Set<JavaFile> javaFiles = Set.of(javaFile);

        AnalysisBounds bounds = qname -> qname.startsWith("test.");

        // Mock calculator that sets a metric value
        List<ClassMetricCalculator> calculators = List.of(
                (classData, metrics) -> metrics.setWmc(99)
        );

        var visitor = new ClassVisitor(javaFiles, bounds, "TestClass.java", calculators);

        // Act
        classDecl.accept(visitor, null);

        // Assert
        QualityMetrics metrics = javaClass.getQualityMetrics();
        assertEquals(99, metrics.getWmc(), "Calculator should have been invoked");
    }

    @Test
    void testMultipleCalculatorsInvoked() {
        // Arrange
        String code = """
                package test;
                public class TestClass {
                    private int field;
                    public void method() {}
                }
                """;
        CompilationUnit cu = StaticJavaParser.parse(code);
        ClassOrInterfaceDeclaration classDecl = cu.getClassByName("TestClass").orElseThrow();

        var javaClass = new JavaClass("test.TestClass");
        var javaFile = new JavaFile("TestClass.java", Set.of(javaClass));
        Set<JavaFile> javaFiles = Set.of(javaFile);

        AnalysisBounds bounds = qname -> qname.startsWith("test.");

        // Multiple calculators
        List<ClassMetricCalculator> calculators = List.of(
                (classData, metrics) -> metrics.setWmc(10),
                (classData, metrics) -> metrics.setDit(5),
                (classData, metrics) -> metrics.setCbo(3)
        );

        var visitor = new ClassVisitor(javaFiles, bounds, "TestClass.java", calculators);

        // Act
        classDecl.accept(visitor, null);

        // Assert
        QualityMetrics metrics = javaClass.getQualityMetrics();
        assertEquals(10, metrics.getWmc(), "First calculator should have been invoked");
        assertEquals(5, metrics.getDit(), "Second calculator should have been invoked");
        assertEquals(3, metrics.getCbo(), "Third calculator should have been invoked");
    }

    @Test
    void testAnalysisBounds_OnlyInBoundsDependenciesCaptured() {
        // Arrange
        String code = """
                package test;
                import test.ProjectClass;
                import external.ExternalClass;

                public class TestClass {
                    private ProjectClass projectField;
                    private ExternalClass externalField;
                }
                """;
        
        CompilationUnit cu = StaticJavaParser.parse(code);

        ClassOrInterfaceDeclaration classDecl = cu.getClassByName("TestClass").orElseThrow();

        JavaClass javaClass = new JavaClass("test.TestClass");
        JavaFile javaFile = new JavaFile("TestClass.java", Set.of(javaClass));
        Set<JavaFile> javaFiles = Set.of(javaFile);

        // Only "test" package is in bounds
        AnalysisBounds bounds = qname -> qname.startsWith("test.");
        List<ClassMetricCalculator> calculators = Collections.emptyList();

        var visitor = new ClassVisitor(javaFiles, bounds, "TestClass.java", calculators);

        // Act
        classDecl.accept(visitor, null);

        // Assert
        ClassData classData = javaClass.getClassData();
        assertNotNull(classData);

        // The visitor only tracks dependencies that are within bounds
        // External dependencies should not be captured
        classData.getDependencies().forEach(dep ->
            assertTrue(dep.startsWith("test."),
                "Only project dependencies should be captured, found: " + dep)
        );
    }

    @Test
    void testIsProjectClass_SetCorrectly() {
        // Arrange
        String code = "package test; public class TestClass {}";
        CompilationUnit cu = StaticJavaParser.parse(code);
        ClassOrInterfaceDeclaration classDecl = cu.getClassByName("TestClass").orElseThrow();

        var javaClass = new JavaClass("test.TestClass");
        var javaFile = new JavaFile("TestClass.java", Set.of(javaClass));
        Set<JavaFile> javaFiles = Set.of(javaFile);

        AnalysisBounds bounds = qname -> qname.startsWith("test.");
        List<ClassMetricCalculator> calculators = Collections.emptyList();

        var visitor = new ClassVisitor(javaFiles, bounds, "TestClass.java", calculators);

        // Act
        classDecl.accept(visitor, null);

        // Assert
        ClassData classData = javaClass.getClassData();
        assertTrue(classData.isProjectClass(), "Class should be marked as project class");
    }

    @Test
    void testClassNotInFile_NotProcessed() {
        // Arrange
        String code = "package test; public class TestClass {}";
        CompilationUnit cu = StaticJavaParser.parse(code);
        ClassOrInterfaceDeclaration classDecl = cu.getClassByName("TestClass").orElseThrow();

        var javaClass = new JavaClass("test.TestClass");
        var javaFile = new JavaFile("DifferentFile.java", Set.of(javaClass)); // Different file!
        Set<JavaFile> javaFiles = Set.of(javaFile);

        AnalysisBounds bounds = qname -> qname.startsWith("test.");
        List<ClassMetricCalculator> calculators = Collections.emptyList();

        var visitor = new ClassVisitor(javaFiles, bounds, "TestClass.java", calculators);

        // Act
        classDecl.accept(visitor, null);

        // Assert
        assertNull(javaClass.getClassData(), "ClassData should not be set for class in different file");
    }
}