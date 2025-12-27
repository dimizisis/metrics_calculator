package calculator.aggregate.impl.coupling;

import context.ClassData;
import infrastructure.entities.JavaClass;
import repository.InMemoryMetricsRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoccCalculatorTest {

    @Test
    void testNoInheritance_NoccRemainsZero() {
        // Arrange
        JavaClass classA = createClassWithData("com.example.ClassA", true);
        JavaClass classB = createClassWithData("com.example.ClassB", true);

        var repository = new InMemoryMetricsRepository(List.of(classA, classB));
        var calculator = new NoccCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(0, classA.getQualityMetrics().getNocc());
        assertEquals(0, classB.getQualityMetrics().getNocc());
    }

    @Test
    void testSingleChildClass_ParentNoccIsOne() {
        // Arrange
        var parentData = ClassData.builder("com.example.Parent")
                .isProjectClass(true)
                .build();
        var parent = new JavaClass("com.example.Parent");
        parent.setClassData(parentData);

        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addDirectParent("com.example.Parent")
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(List.of(parent, child));
        var calculator = new NoccCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(1, parent.getQualityMetrics().getNocc(), "Parent should have NOCC=1");
        assertEquals(0, child.getQualityMetrics().getNocc(), "Child should have NOCC=0");
    }

    @Test
    void testMultipleChildClasses_ParentNoccIncrements() {
        // Arrange
        var parentData = ClassData.builder("com.example.Parent")
                .isProjectClass(true)
                .build();
        var parent = new JavaClass("com.example.Parent");
        parent.setClassData(parentData);

        var child1Data = ClassData.builder("com.example.Child1")
                .isProjectClass(true)
                .addDirectParent("com.example.Parent")
                .build();
        var child1 = new JavaClass("com.example.Child1");
        child1.setClassData(child1Data);

        var child2Data = ClassData.builder("com.example.Child2")
                .isProjectClass(true)
                .addDirectParent("com.example.Parent")
                .build();
        var child2 = new JavaClass("com.example.Child2");
        child2.setClassData(child2Data);

        var child3Data = ClassData.builder("com.example.Child3")
                .isProjectClass(true)
                .addDirectParent("com.example.Parent")
                .build();
        var child3 = new JavaClass("com.example.Child3");
        child3.setClassData(child3Data);

        var repository = new InMemoryMetricsRepository(
                List.of(parent, child1, child2, child3)
        );
        var calculator = new NoccCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(3, parent.getQualityMetrics().getNocc(), "Parent should have NOCC=3");
        assertEquals(0, child1.getQualityMetrics().getNocc());
        assertEquals(0, child2.getQualityMetrics().getNocc());
        assertEquals(0, child3.getQualityMetrics().getNocc());
    }

    @Test
    void testExternalParentNotInProject_NoccNotIncremented() {
        // Arrange
        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addDirectParent("java.lang.Object") // External parent
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(List.of(child));
        var calculator = new NoccCalculator();

        // Act
        calculator.compute(repository);

        // Assert - No exception, parent doesn't exist in repository
        assertEquals(0, child.getQualityMetrics().getNocc());
    }

    @Test
    void testMultiLevelInheritance_EachLevelCountedSeparately() {
        // Arrange
        var grandParentData = ClassData.builder("com.example.GrandParent")
                .isProjectClass(true)
                .build();
        var grandParent = new JavaClass("com.example.GrandParent");
        grandParent.setClassData(grandParentData);

        var parentData = ClassData.builder("com.example.Parent")
                .isProjectClass(true)
                .addDirectParent("com.example.GrandParent")
                .build();
        var parent = new JavaClass("com.example.Parent");
        parent.setClassData(parentData);

        var childData = ClassData.builder("com.example.Child")
                .isProjectClass(true)
                .addDirectParent("com.example.Parent")
                .build();
        var child = new JavaClass("com.example.Child");
        child.setClassData(childData);

        var repository = new InMemoryMetricsRepository(
                List.of(grandParent, parent, child)
        );
        var calculator = new NoccCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(1, grandParent.getQualityMetrics().getNocc(), "GrandParent should have NOCC=1 (parent)");
        assertEquals(1, parent.getQualityMetrics().getNocc(), "Parent should have NOCC=1 (child)");
        assertEquals(0, child.getQualityMetrics().getNocc(), "Child should have NOCC=0");
    }

    @Test
    void testMultipleParents_EachParentCounted() {
        // Arrange (class implementing multiple interfaces)
        var interface1Data = ClassData.builder("com.example.Interface1")
                .isProjectClass(true)
                .build();
        var interface1 = new JavaClass("com.example.Interface1");
        interface1.setClassData(interface1Data);

        var interface2Data = ClassData.builder("com.example.Interface2")
                .isProjectClass(true)
                .build();
        var interface2 = new JavaClass("com.example.Interface2");
        interface2.setClassData(interface2Data);

        var implData = ClassData.builder("com.example.Implementation")
                .isProjectClass(true)
                .addDirectParent("com.example.Interface1")
                .addDirectParent("com.example.Interface2")
                .build();
        var impl = new JavaClass("com.example.Implementation");
        impl.setClassData(implData);

        var repository = new InMemoryMetricsRepository(
                List.of(interface1, interface2, impl)
        );
        var calculator = new NoccCalculator();

        // Act
        calculator.compute(repository);

        // Assert
        assertEquals(1, interface1.getQualityMetrics().getNocc());
        assertEquals(1, interface2.getQualityMetrics().getNocc());
        assertEquals(0, impl.getQualityMetrics().getNocc());
    }

    private JavaClass createClassWithData(String qualifiedName, boolean isProjectClass) {
        ClassData data = ClassData.builder(qualifiedName)
                .isProjectClass(isProjectClass)
                .build();
        JavaClass javaClass = new JavaClass(qualifiedName);
        javaClass.setClassData(data);
        return javaClass;
    }
}