package calculator.single.impl.coupling;

import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CboCalculatorTest {

    @Test
    void testNoDependencies_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CboCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0.0, metrics.getCbo());
        assertEquals(0.0, metrics.getDcc());
    }

    @Test
    void testMultipleDependencies_ReturnsCorrectCount() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addDependency("com.example.ClassA")
                .addDependency("com.example.ClassB")
                .addDependency("com.example.ClassC")
                .addDependency("com.example.ClassD")
                .addDependency("com.example.ClassE")
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CboCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(5.0, metrics.getCbo());
        assertEquals(5.0, metrics.getDcc(), "DCC should equal CBO");
    }

    @Test
    void testDccEqualsCbo() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addDependency("com.example.Dependency1")
                .addDependency("com.example.Dependency2")
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CboCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(metrics.getCbo(), metrics.getDcc());
    }
}