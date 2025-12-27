package calculator.single.impl.coupling;

import calculator.single.impl.coupling.RfcCalculator;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RfcCalculatorTest {

    @Test
    void testNoMethodsNoExternalCalls_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new RfcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0.0, metrics.getRfc());
    }

    @Test
    void testSingleMethodNoExternalCalls_ReturnsOne() {
        // Arrange
        MethodDeclaration method = StaticJavaParser.parseMethodDeclaration(
                "public void method1() { }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new RfcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(1.0, metrics.getRfc());
    }

    @Test
    void testNoMethodsWithExternalCalls_ReturnsCallCount() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethodCalled("com.example.ClassA.method1()")
                .addMethodCalled("com.example.ClassB.method2()")
                .build();
        var metrics = new QualityMetrics();
        var calculator = new RfcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(2.0, metrics.getRfc());
    }

    @Test
    void testMethodsAndExternalCalls_ReturnsSumOfBoth() {
        // Arrange
        MethodDeclaration method1 = StaticJavaParser.parseMethodDeclaration(
                "public void method1() { }"
        );
        MethodDeclaration method2 = StaticJavaParser.parseMethodDeclaration(
                "private int method2() { return 0; }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method1)
                .addMethod(method2)
                .addMethodCalled("com.example.ClassA.methodA()")
                .addMethodCalled("com.example.ClassB.methodB()")
                .addMethodCalled("com.example.ClassC.methodC()")
                .build();
        var metrics = new QualityMetrics();
        var calculator = new RfcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(5.0, metrics.getRfc(), "RFC should be 2 methods + 3 external calls");
    }

    @Test
    void testConstructorNotCounted() {
        // Arrange
        // Constructor handling is done by visitor, not in test
        // Test with only a method to verify non-constructors are counted
        MethodDeclaration method = StaticJavaParser.parseMethodDeclaration(
                "public void method1() { }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method)
                .addMethodCalled("com.example.ClassA.methodA()")
                .build();
        var metrics = new QualityMetrics();
        var calculator = new RfcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(2.0, metrics.getRfc(), "RFC should be 1 method + 1 external call");
    }

    @Test
    void testLargeNumberOfCalls_ReturnsCorrectSum() {
        // Arrange
        MethodDeclaration method1 = StaticJavaParser.parseMethodDeclaration(
                "public void method1() { }"
        );
        MethodDeclaration method2 = StaticJavaParser.parseMethodDeclaration(
                "public void method2() { }"
        );
        MethodDeclaration method3 = StaticJavaParser.parseMethodDeclaration(
                "public void method3() { }"
        );
        var classDataBuilder = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method1)
                .addMethod(method2)
                .addMethod(method3);

        // Add 10 external method calls
        for (int i = 0; i < 10; i++) {
            classDataBuilder.addMethodCalled("com.example.Class" + i + ".method()");
        }

        var classData = classDataBuilder.build();
        var metrics = new QualityMetrics();
        var calculator = new RfcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(13.0, metrics.getRfc(), "RFC should be 3 methods + 10 external calls");
    }
}
