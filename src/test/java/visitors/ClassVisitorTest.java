package visitors;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import infrastructure.entities.JavaClass;
import infrastructure.entities.JavaFile;
import infrastructure.metrics.QualityMetrics;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ClassVisitorTest {

    private static void configureParser() {
        CombinedTypeSolver solver = new CombinedTypeSolver();
        solver.add(new ReflectionTypeSolver());
        solver.add(new JavaParserTypeSolver(new File("src/main/java")));

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(solver);
        ParserConfiguration parserConfiguration = new ParserConfiguration()
                .setSymbolResolver(symbolSolver)
                .setAttributeComments(false)
                .setDetectOriginalLineSeparator(true);

        StaticJavaParser.setConfiguration(parserConfiguration);
    }

    public static Stream<TestCase> provideJavaClasses() {
        return Stream.of(
                new TestCase(
                        "class SimpleClass { public void a() {} public void b() { a(); } }",
                        2, // WMC
                        3, // RFC: 2 declared + call to a()
                        0, // LCOM
                        0  // DIT (no superclass)
                ),
                new TestCase(
                        "class UnusedFields { private int x; private int y; public void a() { x++; } public void b() {} }",
                        2,
                        2,
                        0,
                        0
                ),
                new TestCase(
                        "class Inherited extends java.util.ArrayList { public void a() {} }",
                        1,
                        1,
                        0,
                        0
                ),
                new TestCase(
                        "class ParamMethods { public void a(int x) {} public void b(String s) {} }",
                        2,
                        2,
                        0,
                        0
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideJavaClasses")
    void testVariousClassMetrics(TestCase testCase) {
        configureParser();

        CompilationUnit cu = StaticJavaParser.parse("package test;\npublic " + testCase.classBody);
        ClassOrInterfaceDeclaration clazz = cu.getClassByName("test").orElse(cu.getTypes().get(0).asClassOrInterfaceDeclaration());

        JavaClass jc = new JavaClass("test." + clazz.getNameAsString());
        JavaFile jf = new JavaFile("Sample.java", Set.of(jc));
        Set<JavaFile> javaFiles = Set.of(jf);

        ClassVisitor visitor = new ClassVisitor(javaFiles, "Sample.java", clazz);
        clazz.accept(visitor, null);

        QualityMetrics qm = jc.getQualityMetrics();

        assertEquals(testCase.expectedWmc, qm.getWmc(), "WMC mismatch");
        assertEquals(testCase.expectedRfc, qm.getRfc(), "RFC mismatch");
        assertEquals(testCase.expectedLcom, qm.getLcom(), "LCOM mismatch");
        assertEquals(testCase.expectedDit, qm.getDit(), "DIT mismatch");
    }

    record TestCase(
            String classBody,
            int expectedWmc,
            int expectedRfc,
            double expectedLcom,
            int expectedDit
    ) {}
}
