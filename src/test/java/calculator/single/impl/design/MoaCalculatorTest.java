package calculator.single.impl.design;

import calculator.single.impl.design.MoaCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MoaCalculatorTest {

    @Test
    void testNoFields_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .projectTypeFieldCount(0)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new MoaCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0, metrics.getMoa());
    }

    @Test
    void testSingleProjectTypeField_ReturnsOne() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .projectTypeFieldCount(1)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new MoaCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(1, metrics.getMoa());
    }

    @Test
    void testMultipleProjectTypeFields_ReturnsCorrectCount() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .projectTypeFieldCount(5)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new MoaCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(5, metrics.getMoa());
    }

    @Test
    void testTenProjectTypeFields_ReturnsTen() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .projectTypeFieldCount(10)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new MoaCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(10, metrics.getMoa());
    }
}
