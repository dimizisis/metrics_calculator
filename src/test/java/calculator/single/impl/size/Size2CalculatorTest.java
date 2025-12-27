package calculator.single.impl.size;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Size2CalculatorTest {

    @Test
    void testNoFieldsOrMethods_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .fieldCount(0)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new Size2Calculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0, metrics.getSize2());
    }

    @Test
    void testFieldsAndMethods_ReturnsSum() {
        // Arrange
        MethodDeclaration method1 = StaticJavaParser.parseMethodDeclaration("public void method1() {}");
        MethodDeclaration method2 = StaticJavaParser.parseMethodDeclaration("public void method2() {}");

        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .fieldCount(3) // 3 fields
                .addMethod(method1)
                .addMethod(method2) // 2 methods
                .build();
        var metrics = new QualityMetrics();
        var calculator = new Size2Calculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(5, metrics.getSize2()); // 3 fields + 2 methods
    }

    @Test
    void testOnlyFields_ReturnsFieldCount() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .fieldCount(7)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new Size2Calculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(7, metrics.getSize2());
    }
}