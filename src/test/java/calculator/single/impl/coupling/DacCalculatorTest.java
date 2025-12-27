package calculator.single.impl.coupling;

import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DacCalculatorTest {

    @Test
    void testNoFields_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .dataAbstractionCoupling(0)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DacCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0, metrics.getDac());
    }

    @Test
    void testMultipleNonPrimitiveFields_ReturnsCorrectCount() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .dataAbstractionCoupling(4)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DacCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(4, metrics.getDac());
    }
}