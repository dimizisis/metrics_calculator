package calculator.single.impl.complexity;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CyclomaticComplexityCalculatorTest {

    @Test
    void testNoMethods_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CyclomaticComplexityCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0.0, metrics.getComplexity());
    }

    @Test
    void testSimpleMethodNoConditionals_ReturnsOne() {
        // Arrange
        MethodDeclaration method = StaticJavaParser.parseMethodDeclaration(
                "public void simpleMethod() { System.out.println(\"test\"); }"
        );

        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CyclomaticComplexityCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(1.0, metrics.getComplexity());
    }

    @Test
    void testMethodWithIfStatement_ReturnsTwo() {
        // Arrange
        MethodDeclaration method = StaticJavaParser.parseMethodDeclaration(
                "public void methodWithIf(int x) { if (x > 0) { System.out.println(\"positive\"); } }"
        );

        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CyclomaticComplexityCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(2.0, metrics.getComplexity()); // base 1 + 1 if = 2
    }

    @Test
    void testMultipleMethodsAveragesComplexity() {
        // Arrange
        MethodDeclaration simpleMethod = StaticJavaParser.parseMethodDeclaration(
                "public void simple() { return; }"
        );
        MethodDeclaration complexMethod = StaticJavaParser.parseMethodDeclaration(
                "public void complex(int x) { if (x > 0) { if (x < 10) { return; } } }"
        );

        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(simpleMethod)    // CC = 1
                .addMethod(complexMethod)   // CC = 3 (1 + 2 ifs)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CyclomaticComplexityCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(2.0, metrics.getComplexity()); // (1 + 3) / 2 = 2
    }

    @Test
    void testMethodWithMultipleIfs_ReturnsCorrectComplexity() {
        // Arrange
        MethodDeclaration method = StaticJavaParser.parseMethodDeclaration(
                "public void complexMethod(int x, int y) { " +
                "  if (x > 0) { " +
                "    if (y > 0) { " +
                "      return; " +
                "    } " +
                "  } " +
                "  if (x < 0) { " +
                "    return; " +
                "  } " +
                "}"
        );

        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(method)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CyclomaticComplexityCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(4.0, metrics.getComplexity()); // base 1 + 3 ifs = 4
    }
}