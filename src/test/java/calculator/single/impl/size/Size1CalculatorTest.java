package calculator.single.impl.size;

import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Size1CalculatorTest {

    @Test
    void testNoMembers_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .totalMemberLines(0)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new Size1Calculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0, metrics.getSize1());
    }

    @Test
    void testMultipleMemberLines_ReturnsCorrectCount() {
        // Arrange
        ClassData classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .totalMemberLines(42)
                .build();
        QualityMetrics metrics = new QualityMetrics();
        Size1Calculator calculator = new Size1Calculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(42, metrics.getSize1());
    }
}