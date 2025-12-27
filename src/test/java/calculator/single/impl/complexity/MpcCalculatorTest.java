package calculator.single.impl.complexity;

import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MpcCalculatorTest {

    @Test
    void testNoMethodCalls_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .messagePassingCoupling(0)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new MpcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0, metrics.getMpc());
    }

    @Test
    void testMultipleMethodCalls_ReturnsCorrectCount() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .messagePassingCoupling(7)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new MpcCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(7, metrics.getMpc());
    }
}