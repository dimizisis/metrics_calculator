package calculator.single.impl.size;

import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DscCalculatorTest {

    @Test
    void testNoInnerClasses_ReturnsOne() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .innerClassCount(0)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DscCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(1, metrics.getDsc()); // 1 for the outer class itself
    }

    @Test
    void testMultipleInnerClasses_ReturnsCorrectCount() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .innerClassCount(3)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DscCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(4, metrics.getDsc()); // 1 outer + 3 inner = 4
    }

    @Test
    void testOneInnerClass_ReturnsTwo() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .innerClassCount(1)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DscCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(2, metrics.getDsc());
    }
}