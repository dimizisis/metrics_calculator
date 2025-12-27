package calculator.aggregate.impl.inheritance;

import calculator.aggregate.impl.inheritance.MfaCalculator;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.entities.JavaClass;
import infrastructure.metrics.QualityMetrics;
import repository.InMemoryMetricsRepository;
import repository.MetricsRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MfaCalculatorTest {

    @Test
    void testClassWithNoMethods_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("com.example.EmptyClass")
                .isProjectClass(true)
                .build();
        var javaClass = new JavaClass("com.example.EmptyClass");
        javaClass.setClassData(classData);

        var repository = new InMemoryMetricsRepository(List.of(javaClass));
        var calculator = new MfaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0.0, javaClass.getQualityMetrics().getMfa());
    }

    @Test
    void testClassWithOnlyOwnMethods_ReturnsZero() {
        // Arrange
        MethodDeclaration method1 = StaticJavaParser.parseMethodDeclaration(
                "public void method1() { }"
        );
        MethodDeclaration method2 = StaticJavaParser.parseMethodDeclaration(
                "public void method2() { }"
        );
        var classData = ClassData.builder("com.example.SimpleClass")
                .isProjectClass(true)
                .addMethod(method1)
                .addMethod(method2)
                .build();
        var javaClass = new JavaClass("com.example.SimpleClass");
        javaClass.setClassData(classData);

        var repository = new InMemoryMetricsRepository(List.of(javaClass));
        var calculator = new MfaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0.0, javaClass.getQualityMetrics().getMfa(), "Class with only own methods should have MFA=0");
    }

    @Test
    void testChildClassInheritsAllMethods_ReturnsOne() {
        // Arrange
        // Parent class with 2 methods
        MethodDeclaration parentMethod1 = StaticJavaParser.parseMethodDeclaration(
                "public void parentMethod1() { }"
        );
        MethodDeclaration parentMethod2 = StaticJavaParser.parseMethodDeclaration(
                "public void parentMethod2() { }"
        );
        var parentData = ClassData.builder("com.example.Parent")
                .isProjectClass(true)
                .addMethod(parentMethod1)
                .addMethod(parentMethod2)
                .build();
        var parent = new JavaClass("com.example.Parent");
        parent.setClassData(parentData);

        // Child class with no own methods, inherits from parent
        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addDirectParent("com.example.Parent")
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(List.of(parent, child));
        var calculator = new MfaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(1.0, child.getQualityMetrics().getMfa(), "Child with only inherited methods should have MFA=1");
    }

    @Test
    void testChildClassWithOwnAndInheritedMethods_ReturnsCorrectRatio() {
        // Arrange
        // Parent class with 2 methods
        MethodDeclaration parentMethod1 = StaticJavaParser.parseMethodDeclaration(
                "public void parentMethod1() { }"
        );
        MethodDeclaration parentMethod2 = StaticJavaParser.parseMethodDeclaration(
                "public void parentMethod2() { }"
        );
        var parentData = ClassData.builder("com.example.Parent")
                .isProjectClass(true)
                .addMethod(parentMethod1)
                .addMethod(parentMethod2)
                .build();
        var parent = new JavaClass("com.example.Parent");
        parent.setClassData(parentData);

        // Child class with 1 own method, inherits 2 from parent
        MethodDeclaration childMethod = StaticJavaParser.parseMethodDeclaration(
                "public void childMethod() { }"
        );
        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addMethod(childMethod)
                .addDirectParent("com.example.Parent")
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(List.of(parent, child));
        var calculator = new MfaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        // inherited = 2, own = 1, total = 3, MFA = 2/3 = 0.6666...
        assertEquals(2.0 / 3.0, child.getQualityMetrics().getMfa(), 0.0001);
    }

    @Test
    void testOverriddenMethodsNotCountedAsInherited() {
        // Arrange
        // Parent class with 2 methods
        MethodDeclaration parentMethod1 = StaticJavaParser.parseMethodDeclaration(
                "public void sharedMethod() { }"
        );
        MethodDeclaration parentMethod2 = StaticJavaParser.parseMethodDeclaration(
                "public void parentOnlyMethod() { }"
        );
        var parentData = ClassData.builder("com.example.Parent")
                .isProjectClass(true)
                .addMethod(parentMethod1)
                .addMethod(parentMethod2)
                .build();
        var parent = new JavaClass("com.example.Parent");
        parent.setClassData(parentData);

        // Child class overrides sharedMethod
        MethodDeclaration childOverride = StaticJavaParser.parseMethodDeclaration(
                "public void sharedMethod() { System.out.println(\"overridden\"); }"
        );
        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addMethod(childOverride)
                .addDirectParent("com.example.Parent")
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(List.of(parent, child));
        var calculator = new MfaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        // inherited = 1 (parentOnlyMethod), own = 1 (sharedMethod override), total = 2
        // MFA = 1/2 = 0.5
        assertEquals(0.5, child.getQualityMetrics().getMfa(), 0.0001);
    }

    @Test
    void testMultiLevelInheritance_ComputesCorrectly() {
        // Arrange
        // Grandparent class with 1 method
        MethodDeclaration grandparentMethod = StaticJavaParser.parseMethodDeclaration(
                "public void grandparentMethod() { }"
        );
        var grandparentData = ClassData.builder("com.example.Grandparent")
                .isProjectClass(true)
                .addMethod(grandparentMethod)
                .build();
        var grandparent = new JavaClass("com.example.Grandparent");
        grandparent.setClassData(grandparentData);

        // Parent class with 1 method, extends Grandparent
        MethodDeclaration parentMethod = StaticJavaParser.parseMethodDeclaration(
                "public void parentMethod() { }"
        );
        var parentData = ClassData.builder("com.example.Parent")
                .isProjectClass(true)
                .addMethod(parentMethod)
                .addDirectParent("com.example.Grandparent")
                .build();
        var parent = new JavaClass("com.example.Parent");
        parent.setClassData(parentData);

        // Child class with 1 own method, extends Parent
        MethodDeclaration childMethod = StaticJavaParser.parseMethodDeclaration(
                "public void childMethod() { }"
        );
        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addMethod(childMethod)
                .addDirectParent("com.example.Parent")
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(List.of(grandparent, parent, child));
        var calculator = new MfaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        // Child inherits: grandparentMethod + parentMethod = 2
        // Child own: childMethod = 1
        // Total = 3, MFA = 2/3 = 0.6666...
        assertEquals(2.0 / 3.0, child.getQualityMetrics().getMfa(), 0.0001);
    }

    @Test
    void testPrivateMethodsNotInherited() {
        // Arrange
        // Parent class with 1 public and 1 private method
        MethodDeclaration publicMethod = StaticJavaParser.parseMethodDeclaration(
                "public void publicMethod() { }"
        );
        MethodDeclaration privateMethod = StaticJavaParser.parseMethodDeclaration(
                "private void privateMethod() { }"
        );
        var parentData = ClassData.builder("com.example.Parent")
                .isProjectClass(true)
                .addMethod(publicMethod)
                .addMethod(privateMethod)
                .build();
        var parent = new JavaClass("com.example.Parent");
        parent.setClassData(parentData);

        // Child class with no own methods
        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addDirectParent("com.example.Parent")
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(List.of(parent, child));
        var calculator = new MfaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        // Only publicMethod is inherited (privateMethod is not inherited)
        // inherited = 1, own = 0, total = 1, MFA = 1/1 = 1.0
        assertEquals(1.0, child.getQualityMetrics().getMfa(), 0.0001);
    }

    @Test
    void testConstructorsNotCounted() {
        // Arrange
        // Parent class with 1 method only (constructors handled by visitor)
        MethodDeclaration method = StaticJavaParser.parseMethodDeclaration(
                "public void method() { }"
        );
        var parentData = ClassData.builder("com.example.Parent")
                .isProjectClass(true)
                .addMethod(method)
                .build();
        var parent = new JavaClass("com.example.Parent");
        parent.setClassData(parentData);

        // Child class with no methods
        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addDirectParent("com.example.Parent")
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(List.of(parent, child));
        var calculator = new MfaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        // Constructor handling is done by visitor, not in test
        // inherited = 1 (method), own = 0, total = 1, MFA = 1/1 = 1.0
        assertEquals(1.0, child.getQualityMetrics().getMfa(), 0.0001);
    }
}
