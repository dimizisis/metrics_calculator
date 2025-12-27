package calculator.single.impl.encapsulation;

import calculator.single.impl.encapsulation.DamCalculator;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DamCalculatorTest {

    @Test
    void testNoFields_ReturnsMinusOne() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .fieldCount(0)
                .privateProtectedFieldCount(0)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DamCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(-1.0, metrics.getDam());
    }

    @Test
    void testAllPublicFields_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .fieldCount(3)
                .publicFieldCount(3)
                .privateProtectedFieldCount(0)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DamCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0.0, metrics.getDam());
    }

    @Test
    void testAllPrivateFields_ReturnsOne() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .fieldCount(5)
                .publicFieldCount(0)
                .privateProtectedFieldCount(5)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DamCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(1.0, metrics.getDam());
    }

    @Test
    void testMixedFields_ComputesCorrectRatio() {
        // Arrange
        // 2 private + 1 protected = 3 private/protected
        // 1 public
        // total = 4
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .fieldCount(4)
                .publicFieldCount(1)
                .privateProtectedFieldCount(3)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DamCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0.75, metrics.getDam());
    }

    @Test
    void testHalfPrivateHalfPublic_ReturnsHalf() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .fieldCount(10)
                .publicFieldCount(5)
                .privateProtectedFieldCount(5)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DamCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0.5, metrics.getDam());
    }

    @Test
    void testSinglePrivateField_ReturnsOne() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .fieldCount(1)
                .publicFieldCount(0)
                .privateProtectedFieldCount(1)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DamCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(1.0, metrics.getDam());
    }

    @Test
    void testSinglePublicField_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .fieldCount(1)
                .publicFieldCount(1)
                .privateProtectedFieldCount(0)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new DamCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0.0, metrics.getDam());
    }
}
