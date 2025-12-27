package calculator.aggregate.impl.inheritance;

import calculator.aggregate.impl.inheritance.NohCalculator;
import context.ClassData;
import infrastructure.entities.JavaClass;
import infrastructure.metrics.QualityMetrics;
import repository.InMemoryMetricsRepository;
import repository.MetricsRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NohCalculatorTest {

    @Test
    void testClassWithNoChildrenNoAncestors_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("com.example.StandaloneClass")
                .isProjectClass(true)
                .build();
        var javaClass = new JavaClass("com.example.StandaloneClass");
        javaClass.setClassData(classData);

        QualityMetrics metrics = javaClass.getQualityMetrics();
        metrics.setNocc(0); // No children
        metrics.setAna(0);  // No ancestors

        var repository = new InMemoryMetricsRepository(List.of(javaClass));
        var calculator = new NohCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0, metrics.getNoh(), "Class with no children and no ancestors should have NOH=0");
    }

    @Test
    void testRootClassWithChildren_ReturnsOne() {
        // Arrange
        var classData = ClassData.builder("com.example.RootClass")
                .isProjectClass(true)
                .build();
        var javaClass = new JavaClass("com.example.RootClass");
        javaClass.setClassData(classData);

        QualityMetrics metrics = javaClass.getQualityMetrics();
        metrics.setNocc(2); // Has 2 children
        metrics.setAna(0);  // No ancestors (root class)

        var repository = new InMemoryMetricsRepository(List.of(javaClass));
        var calculator = new NohCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(1, metrics.getNoh(), "Root class with children should have NOH=1");
    }

    @Test
    void testMiddleClassWithChildrenAndAncestors_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("com.example.MiddleClass")
                .isProjectClass(true)
                .build();
        var javaClass = new JavaClass("com.example.MiddleClass");
        javaClass.setClassData(classData);

        QualityMetrics metrics = javaClass.getQualityMetrics();
        metrics.setNocc(1); // Has children
        metrics.setAna(1);  // Has ancestors

        var repository = new InMemoryMetricsRepository(List.of(javaClass));
        var calculator = new NohCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0, metrics.getNoh(), "Middle class with both children and ancestors should have NOH=0");
    }

    @Test
    void testLeafClassWithAncestorsNoChildren_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("com.example.LeafClass")
                .isProjectClass(true)
                .build();
        var javaClass = new JavaClass("com.example.LeafClass");
        javaClass.setClassData(classData);

        QualityMetrics metrics = javaClass.getQualityMetrics();
        metrics.setNocc(0); // No children (leaf)
        metrics.setAna(2);  // Has ancestors

        var repository = new InMemoryMetricsRepository(List.of(javaClass));
        var calculator = new NohCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0, metrics.getNoh(), "Leaf class with ancestors should have NOH=0");
    }

    @Test
    void testMultipleClasses_ComputesCorrectly() {
        // Arrange
        // Root class (NOH = 1)
        var rootData = ClassData.builder("com.example.Root")
                .isProjectClass(true)
                .build();
        var root = new JavaClass("com.example.Root");
        root.setClassData(rootData);
        root.getQualityMetrics().setNocc(2);
        root.getQualityMetrics().setAna(0);

        // Middle class (NOH = 0)
        var middleData = ClassData.builder("com.example.Middle")
                .isProjectClass(true)
                .build();
        var middle = new JavaClass("com.example.Middle");
        middle.setClassData(middleData);
        middle.getQualityMetrics().setNocc(1);
        middle.getQualityMetrics().setAna(1);

        // Leaf class (NOH = 0)
        var leafData = ClassData.builder("com.example.Leaf")
                .isProjectClass(true)
                .build();
        var leaf = new JavaClass("com.example.Leaf");
        leaf.setClassData(leafData);
        leaf.getQualityMetrics().setNocc(0);
        leaf.getQualityMetrics().setAna(2);

        // Standalone class (NOH = 0)
        var standaloneData = ClassData.builder("com.example.Standalone")
                .isProjectClass(true)
                .build();
        var standalone = new JavaClass("com.example.Standalone");
        standalone.setClassData(standaloneData);
        standalone.getQualityMetrics().setNocc(0);
        standalone.getQualityMetrics().setAna(0);

        var repository = new InMemoryMetricsRepository(List.of(root, middle, leaf, standalone));
        var calculator = new NohCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(1, root.getQualityMetrics().getNoh(), "Root should have NOH=1");
        assertEquals(0, middle.getQualityMetrics().getNoh(), "Middle should have NOH=0");
        assertEquals(0, leaf.getQualityMetrics().getNoh(), "Leaf should have NOH=0");
        assertEquals(0, standalone.getQualityMetrics().getNoh(), "Standalone should have NOH=0");
    }

    @Test
    void testRootClassWithOneChild_ReturnsOne() {
        // Arrange
        var classData = ClassData.builder("com.example.RootClass")
                .isProjectClass(true)
                .build();
        var javaClass = new JavaClass("com.example.RootClass");
        javaClass.setClassData(classData);

        QualityMetrics metrics = javaClass.getQualityMetrics();
        metrics.setNocc(1); // Has 1 child (minimum to be root)
        metrics.setAna(0);  // No ancestors

        var repository = new InMemoryMetricsRepository(List.of(javaClass));
        var calculator = new NohCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(1, metrics.getNoh(), "Root class with even one child should have NOH=1");
    }
}
