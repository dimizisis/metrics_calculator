package calculator.single.impl.inheritance;

import calculator.single.impl.inheritance.NopCalculator;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NopCalculatorTest {

    @Test
    void testNoMethods_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new NopCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0, metrics.getNop());
    }

    @Test
    void testSingleAbstractMethod_ReturnsOne() {
        // Arrange
        MethodDeclaration abstractMethod = StaticJavaParser.parseMethodDeclaration(
                "public abstract void doSomething();"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(abstractMethod)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new NopCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(1, metrics.getNop());
    }

    @Test
    void testMultipleAbstractMethods_ReturnsCorrectCount() {
        // Arrange
        MethodDeclaration abstractMethod1 = StaticJavaParser.parseMethodDeclaration(
                "public abstract void method1();"
        );
        MethodDeclaration abstractMethod2 = StaticJavaParser.parseMethodDeclaration(
                "protected abstract int method2();"
        );
        MethodDeclaration abstractMethod3 = StaticJavaParser.parseMethodDeclaration(
                "abstract String method3();"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(abstractMethod1)
                .addMethod(abstractMethod2)
                .addMethod(abstractMethod3)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new NopCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(3, metrics.getNop());
    }

    @Test
    void testMixedAbstractAndConcrete_CountsOnlyAbstract() {
        // Arrange
        MethodDeclaration abstractMethod = StaticJavaParser.parseMethodDeclaration(
                "public abstract void abstractMethod();"
        );
        MethodDeclaration concreteMethod1 = StaticJavaParser.parseMethodDeclaration(
                "public void concreteMethod1() { }"
        );
        MethodDeclaration concreteMethod2 = StaticJavaParser.parseMethodDeclaration(
                "private int concreteMethod2() { return 0; }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(abstractMethod)
                .addMethod(concreteMethod1)
                .addMethod(concreteMethod2)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new NopCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(1, metrics.getNop(), "Should count only abstract methods");
    }

    @Test
    void testOnlyConcreteMethod_ReturnsZero() {
        // Arrange
        MethodDeclaration concreteMethod = StaticJavaParser.parseMethodDeclaration(
                "public void concreteMethod() { System.out.println(\"Hello\"); }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(concreteMethod)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new NopCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0, metrics.getNop());
    }
}
