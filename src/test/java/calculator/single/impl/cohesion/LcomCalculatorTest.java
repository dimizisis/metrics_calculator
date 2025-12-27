package calculator.single.impl.cohesion;

import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class LcomCalculatorTest {

    @Test
    void testNoMethods_ReturnsNegativeOne() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new LcomCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(-1.0, metrics.getLcom());
    }

    @Test
    void testFullyCohesive_ReturnsZero() {
        // Arrange - All methods access same fields
        var fields1 = new TreeSet<>(Set.of("field1", "field2"));
        var fields2 = new TreeSet<>(Set.of("field1", "field2"));
        var fields3 = new TreeSet<>(Set.of("field1", "field2"));

        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethodFieldSet(fields1)
                .addMethodFieldSet(fields2)
                .addMethodFieldSet(fields3)
                .build();

        var metrics = new QualityMetrics();
        var calculator = new LcomCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0.0, metrics.getLcom());
    }

    @Test
    void testNoCohesion_ReturnsPositive() {
        // Arrange - No methods share fields
        var fields1 = new TreeSet<>(Set.of("field1"));
        var fields2 = new TreeSet<>(Set.of("field2"));
        var fields3 = new TreeSet<>(Set.of("field3"));

        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethodFieldSet(fields1)
                .addMethodFieldSet(fields2)
                .addMethodFieldSet(fields3)
                .build();

        var metrics = new QualityMetrics();
        var calculator = new LcomCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertTrue(metrics.getLcom() > 0, "LCOM should be positive for non-cohesive class");
        assertEquals(3.0, metrics.getLcom()); // 3 pairs with no shared fields
    }

    @Test
    void testEmptyFieldSets_ReturnsZero() {
        // Arrange - Methods exist but access no fields
        var emptyFields = new TreeSet<String>();

        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethodFieldSet(emptyFields)
                .addMethodFieldSet(emptyFields)
                .build();

        QualityMetrics metrics = new QualityMetrics();
        LcomCalculator calculator = new LcomCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0.0, metrics.getLcom());
    }
}
