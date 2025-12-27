package calculator.single.impl.encapsulation;

import calculator.single.impl.encapsulation.CisCalculator;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.MethodDeclaration;
import context.ClassData;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CisCalculatorTest {

    @Test
    void testNoMethods_ReturnsZero() {
        // Arrange
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CisCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(0, metrics.getCis());
        assertEquals(0, metrics.getNpm()); // NPM should also be set
    }

    @Test
    void testSinglePublicMethod_ReturnsOne() {
        // Arrange
        MethodDeclaration publicMethod = StaticJavaParser.parseMethodDeclaration(
                "public void doSomething() { }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(publicMethod)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CisCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(1, metrics.getCis());
        assertEquals(1, metrics.getNpm());
    }

    @Test
    void testMultiplePublicMethods_ReturnsCorrectCount() {
        // Arrange
        MethodDeclaration publicMethod1 = StaticJavaParser.parseMethodDeclaration(
                "public void method1() { }"
        );
        MethodDeclaration publicMethod2 = StaticJavaParser.parseMethodDeclaration(
                "public int method2() { return 0; }"
        );
        MethodDeclaration publicMethod3 = StaticJavaParser.parseMethodDeclaration(
                "public String method3() { return null; }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(publicMethod1)
                .addMethod(publicMethod2)
                .addMethod(publicMethod3)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CisCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(3, metrics.getCis());
        assertEquals(3, metrics.getNpm());
    }

    @Test
    void testMixedVisibility_CountsOnlyPublic() {
        // Arrange
        MethodDeclaration publicMethod = StaticJavaParser.parseMethodDeclaration(
                "public void publicMethod() { }"
        );
        MethodDeclaration privateMethod = StaticJavaParser.parseMethodDeclaration(
                "private void privateMethod() { }"
        );
        MethodDeclaration protectedMethod = StaticJavaParser.parseMethodDeclaration(
                "protected void protectedMethod() { }"
        );
        MethodDeclaration packagePrivateMethod = StaticJavaParser.parseMethodDeclaration(
                "void packagePrivateMethod() { }"
        );
        var classData = ClassData.builder("TestClass")
                .isProjectClass(true)
                .addMethod(publicMethod)
                .addMethod(privateMethod)
                .addMethod(protectedMethod)
                .addMethod(packagePrivateMethod)
                .build();
        var metrics = new QualityMetrics();
        var calculator = new CisCalculator();

        // Act
        calculator.compute(classData, metrics);

        // Assert
        assertEquals(1, metrics.getCis(), "Should count only public methods");
        assertEquals(1, metrics.getNpm());
    }
}
