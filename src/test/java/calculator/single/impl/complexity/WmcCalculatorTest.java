package calculator.single.impl.complexity;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WmcCalculatorTest {

    @Test
    void testNoMethods_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new WmcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0, metrics.getWmc());
    }

    @Test
    void testThreeRegularMethods_ReturnsThree() {
        // Arrange
        MethodDeclaration method1 = StaticJavaParser.parseMethodDeclaration("public void method1() {}");
        MethodDeclaration method2 = StaticJavaParser.parseMethodDeclaration("public int method2() { return 0; }");
        MethodDeclaration method3 = StaticJavaParser.parseMethodDeclaration("private String method3() { return \"\"; }");

        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method1)
                .addMethod(method2)
                .addMethod(method3)
                .build();

        var metrics = new QualityMetrics();
        var calculator = new WmcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(3, metrics.getWmc());
    }

    @Test
    void testOnlyRegularMethodsCounted() {
        // Arrange - Mix of different method types
        MethodDeclaration method1 = StaticJavaParser.parseMethodDeclaration("public void regularMethod() {}");
        MethodDeclaration method2 = StaticJavaParser.parseMethodDeclaration("private int anotherMethod() { return 42; }");
        MethodDeclaration method3 = StaticJavaParser.parseMethodDeclaration("protected void thirdMethod() {}");

        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method1)
                .addMethod(method2)
                .addMethod(method3)
                .build();

        var metrics = new QualityMetrics();
        var calculator = new WmcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(3, metrics.getWmc(), "All non-constructor methods should be counted");
    }
}