package calculator.single.impl.complexity;

import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DitCalculatorTest {

    @Test
    void testNoInheritance_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .depthOfInheritance(0)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DitCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0, metrics.getDit());
    }

    @Test
    void testSingleLevelInheritance_ReturnsOne() {
        // Arrange
        var classData = ClassData.builder("ChildClass")
                .isProjectClass(true)
                .depthOfInheritance(1)
                .addDirectParent("ParentClass")
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DitCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(1, metrics.getDit());
    }

    @Test
    void testDeepInheritance_ReturnsCorrectDepth() {
        // Arrange
        var classData = ClassData.builder("GrandChildClass")
                .isProjectClass(true)
                .depthOfInheritance(5)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DitCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(5, metrics.getDit());
    }
}