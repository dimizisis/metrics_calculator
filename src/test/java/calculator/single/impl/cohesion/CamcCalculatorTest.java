package calculator.single.impl.cohesion;

import calculator.single.impl.cohesion.CamcCalculator;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CamcCalculatorTest {

    @Test
    void testNoMethods_ReturnsMinusOne() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CamcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(-1.0, metrics.getCamc());
    }

    @Test
    void testMethodsWithNoParameters_ReturnsMinusOne() {
        // Arrange
        MethodDeclaration method1 = StaticJavaParser.parseMethodDeclaration(
                "public void method1() { }"
        );
        MethodDeclaration method2 = StaticJavaParser.parseMethodDeclaration(
                "public void method2() { }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method1)
                .addMethod(method2)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CamcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(-1.0, metrics.getCamc());
    }

    @Test
    void testSingleMethodWithSingleParameter_ReturnsOne() {
        // Arrange
        MethodDeclaration method = StaticJavaParser.parseMethodDeclaration(
                "public void method(int x) { }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CamcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        // numerator = 1 (method has 1 distinct param type)
        // denominator = 1 * 1 (1 method * 1 total distinct param type)
        // CAMC = 1 / 1 = 1.0
        assertEquals(1.0, metrics.getCamc());
    }

    @Test
    void testTwoMethodsSameParameterType_ReturnsOne() {
        // Arrange
        MethodDeclaration method1 = StaticJavaParser.parseMethodDeclaration(
                "public void method1(int x) { }"
        );
        MethodDeclaration method2 = StaticJavaParser.parseMethodDeclaration(
                "public void method2(int y) { }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method1)
                .addMethod(method2)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CamcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        // numerator = 1 + 1 = 2 (each method has 1 distinct param type)
        // denominator = 2 * 1 = 2 (2 methods * 1 total distinct param type)
        // CAMC = 2 / 2 = 1.0
        assertEquals(1.0, metrics.getCamc());
    }

    @Test
    void testTwoMethodsDifferentParameterTypes_ReturnsHalf() {
        // Arrange
        MethodDeclaration method1 = StaticJavaParser.parseMethodDeclaration(
                "public void method1(int x) { }"
        );
        MethodDeclaration method2 = StaticJavaParser.parseMethodDeclaration(
                "public void method2(String s) { }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method1)
                .addMethod(method2)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CamcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        // numerator = 1 + 1 = 2 (each method has 1 distinct param type)
        // denominator = 2 * 2 = 4 (2 methods * 2 total distinct param types: int, String)
        // CAMC = 2 / 4 = 0.5
        assertEquals(0.5, metrics.getCamc());
    }

    @Test
    void testMethodWithMultipleParameters_ComputesCorrectly() {
        // Arrange
        MethodDeclaration method = StaticJavaParser.parseMethodDeclaration(
                "public void method(int x, String s, double d) { }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CamcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        // numerator = 3 (method has 3 distinct param types)
        // denominator = 1 * 3 = 3 (1 method * 3 total distinct param types)
        // CAMC = 3 / 3 = 1.0
        assertEquals(1.0, metrics.getCamc());
    }

    @Test
    void testComplexScenario_ComputesCorrectly() {
        // Arrange
        MethodDeclaration method1 = StaticJavaParser.parseMethodDeclaration(
                "public void method1(int x, String s) { }"
        );
        MethodDeclaration method2 = StaticJavaParser.parseMethodDeclaration(
                "public void method2(String s, double d) { }"
        );
        MethodDeclaration method3 = StaticJavaParser.parseMethodDeclaration(
                "public void method3(int x) { }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method1)
                .addMethod(method2)
                .addMethod(method3)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CamcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        // method1: {int, String} = 2 types
        // method2: {String, double} = 2 types
        // method3: {int} = 1 type
        // numerator = 2 + 2 + 1 = 5
        // total distinct param types = {int, String, double} = 3
        // denominator = 3 * 3 = 9 (3 methods * 3 total distinct param types)
        // CAMC = 5 / 9 = 0.5555...
        assertEquals(5.0 / 9.0, metrics.getCamc(), 0.0001);
    }

    @Test
    void testDuplicateParametersInSameMethod_CountedOnce() {
        // Arrange
        MethodDeclaration method = StaticJavaParser.parseMethodDeclaration(
                "public void method(int x, int y, int z) { }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CamcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        // numerator = 1 (only 1 distinct param type: int)
        // denominator = 1 * 1 = 1
        // CAMC = 1 / 1 = 1.0
        assertEquals(1.0, metrics.getCamc());
    }
}
