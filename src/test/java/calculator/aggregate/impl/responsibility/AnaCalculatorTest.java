package calculator.aggregate.impl.responsibility;

import calculator.aggregate.impl.responsibility.AnaCalculator;
import context.ClassData;
import infrastructure.entities.JavaClass;
import infrastructure.metrics.QualityMetrics;
import repository.InMemoryMetricsRepository;
import repository.MetricsRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnaCalculatorTest {

    @Test
    void testClassWithNoAncestors_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("com.example.StandaloneClass")
                .isProjectClass(true)
                .build();
        var javaClass = new JavaClass("com.example.StandaloneClass");
        javaClass.setClassData(classData);

        var repository = new InMemoryMetricsRepository(List.of(javaClass));
        var calculator = new AnaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0, javaClass.getQualityMetrics().getAna(), "Class with no ancestors should have ANA=0");
    }

    @Test
    void testClassWithSingleAncestor_ReturnsOne() {
        // Arrange
        // Parent class
        var parentData = ClassData.builder("com.example.Parent")
                .isProjectClass(true)
                .build();
        var parent = new JavaClass("com.example.Parent");
        parent.setClassData(parentData);

        // Child class extends Parent
        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addDirectParent("com.example.Parent")
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(List.of(parent, child));
        var calculator = new AnaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0, parent.getQualityMetrics().getAna(), "Parent should have ANA=0");
        assertEquals(1, child.getQualityMetrics().getAna(), "Child should have ANA=1");
    }

    @Test
    void testMultiLevelInheritance_CountsAllAncestors() {
        // Arrange
        // Grandparent
        var grandparentData = ClassData.builder("com.example.Grandparent")
                .isProjectClass(true)
                .build();
        var grandparent = new JavaClass("com.example.Grandparent");
        grandparent.setClassData(grandparentData);

        // Parent extends Grandparent
        var parentData = ClassData.builder("com.example.Parent")
                .isProjectClass(true)
                .addDirectParent("com.example.Grandparent")
                .build();
        var parent = new JavaClass("com.example.Parent");
        parent.setClassData(parentData);

        // Child extends Parent
        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addDirectParent("com.example.Parent")
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(List.of(grandparent, parent, child));
        var calculator = new AnaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0, grandparent.getQualityMetrics().getAna(), "Grandparent should have ANA=0");
        assertEquals(1, parent.getQualityMetrics().getAna(), "Parent should have ANA=1");
        assertEquals(2, child.getQualityMetrics().getAna(), "Child should have ANA=2 (Parent + Grandparent)");
    }

    @Test
    void testMultipleInterfaces_CountsAll() {
        // Arrange
        // Interface A
        var interfaceAData = ClassData.builder("com.example.InterfaceA")
                .isProjectClass(true)
                .build();
        var interfaceA = new JavaClass("com.example.InterfaceA");
        interfaceA.setClassData(interfaceAData);

        // Interface B
        var interfaceBData = ClassData.builder("com.example.InterfaceB")
                .isProjectClass(true)
                .build();
        var interfaceB = new JavaClass("com.example.InterfaceB");
        interfaceB.setClassData(interfaceBData);

        // Class implements both interfaces
        var classData = ClassData.builder("com.example.MyClass")
                .isProjectClass(true)
                .addDirectParent("com.example.InterfaceA")
                .addDirectParent("com.example.InterfaceB")
                .build();
        var myClass = new JavaClass("com.example.MyClass");
        myClass.setClassData(classData);

        var repository = new InMemoryMetricsRepository(List.of(interfaceA, interfaceB, myClass));
        var calculator = new AnaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0, interfaceA.getQualityMetrics().getAna());
        assertEquals(0, interfaceB.getQualityMetrics().getAna());
        assertEquals(2, myClass.getQualityMetrics().getAna(), "Class should have ANA=2 (both interfaces)");
    }

    @Test
    void testComplexHierarchy_CountsCorrectly() {
        // Arrange
        /*
         * Hierarchy:
         *     GrandparentA    GrandparentB
         *           \            /
         *            \          /
         *             ParentA  ParentB
         *                  \  /
         *                   \/
         *                 Child
         */
        var grandparentAData = ClassData.builder("com.example.GrandparentA")
                .isProjectClass(true)
                .build();
        var grandparentA = new JavaClass("com.example.GrandparentA");
        grandparentA.setClassData(grandparentAData);

        var grandparentBData = ClassData.builder("com.example.GrandparentB")
                .isProjectClass(true)
                .build();
        var grandparentB = new JavaClass("com.example.GrandparentB");
        grandparentB.setClassData(grandparentBData);

        var parentAData = ClassData.builder("com.example.ParentA")
                .isProjectClass(true)
                .addDirectParent("com.example.GrandparentA")
                .build();
        var parentA = new JavaClass("com.example.ParentA");
        parentA.setClassData(parentAData);

        var parentBData = ClassData.builder("com.example.ParentB")
                .isProjectClass(true)
                .addDirectParent("com.example.GrandparentB")
                .build();
        var parentB = new JavaClass("com.example.ParentB");
        parentB.setClassData(parentBData);

        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addDirectParent("com.example.ParentA")
                .addDirectParent("com.example.ParentB")
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(List.of(
                grandparentA, grandparentB, parentA, parentB, child
        ));
        var calculator = new AnaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0, grandparentA.getQualityMetrics().getAna());
        assertEquals(0, grandparentB.getQualityMetrics().getAna());
        assertEquals(1, parentA.getQualityMetrics().getAna());
        assertEquals(1, parentB.getQualityMetrics().getAna());
        assertEquals(4, child.getQualityMetrics().getAna(),
                "Child should have ANA=4 (ParentA, ParentB, GrandparentA, GrandparentB)");
    }

    @Test
    void testExternalAncestorsNotCounted() {
        // Arrange
        // Parent is external (not a project class)
        var parentData = ClassData.builder("com.external.Parent")
                .isProjectClass(false)
                .build();
        var parent = new JavaClass("com.external.Parent");
        parent.setClassData(parentData);

        // Child extends external parent
        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addDirectParent("com.external.Parent")
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(List.of(parent, child));
        var calculator = new AnaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0, child.getQualityMetrics().getAna(),
                "Child should have ANA=0 because parent is external");
    }

    @Test
    void testDiamondInheritance_CountsEachAncestorOnce() {
        // Arrange
        /*
         * Diamond:
         *       Top
         *      /   \
         *   Left   Right
         *      \   /
         *     Bottom
         */
        var topData = ClassData.builder("com.example.Top")
                .isProjectClass(true)
                .build();
        var top = new JavaClass("com.example.Top");
        top.setClassData(topData);

        var leftData = ClassData.builder("com.example.Left")
                .isProjectClass(true)
                .addDirectParent("com.example.Top")
                .build();
        var left = new JavaClass("com.example.Left");
        left.setClassData(leftData);

        var rightData = ClassData.builder("com.example.Right")
                .isProjectClass(true)
                .addDirectParent("com.example.Top")
                .build();
        var right = new JavaClass("com.example.Right");
        right.setClassData(rightData);

        var bottomData = ClassData.builder("com.example.Bottom")
                .isProjectClass(true)
                .addDirectParent("com.example.Left")
                .addDirectParent("com.example.Right")
                .build();
        var bottom = new JavaClass("com.example.Bottom");
        bottom.setClassData(bottomData);

        var repository = new InMemoryMetricsRepository(List.of(top, left, right, bottom));
        var calculator = new AnaCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0, top.getQualityMetrics().getAna());
        assertEquals(1, left.getQualityMetrics().getAna());
        assertEquals(1, right.getQualityMetrics().getAna());
        assertEquals(3, bottom.getQualityMetrics().getAna(),
                "Bottom should have ANA=3 (Left, Right, Top counted once)");
    }
}
