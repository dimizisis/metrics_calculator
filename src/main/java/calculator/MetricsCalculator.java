package calculator;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import infrastructure.entities.Class;
import infrastructure.entities.JavaFile;
import infrastructure.entities.Project;
import visitors.ClassVisitor;

import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MetricsCalculator {

    private final Project project;

    public MetricsCalculator(Project project) {
        this.project = project;
    }

    /**
     * Start the whole process
     *
     * @return 0 if everything went ok, -1 otherwise
     */
    public int start() {
        ProjectRoot projectRoot = getProjectRoot(project.getClonePath());
        List<SourceRoot> sourceRoots = projectRoot.getSourceRoots();
        try {
            createSymbolSolver(project.getClonePath());
        } catch (IllegalStateException e) {
            return -1;
        }
        if (createFileSet(sourceRoots) == 0) {
            System.err.println("No classes could be identified! Exiting...");
            return -1;
        }
        startCalculations(sourceRoots);
        performAggregation();
        return 0;
    }

    /**
     * Aggregates quality metrics
     */
    private void performAggregation() {
        project.getJavaFiles().forEach(JavaFile::aggregateMetrics);
    }

    /**
     * Get the project root
     */
    private ProjectRoot getProjectRoot(String projectDir) {
        System.out.println("Collecting source roots...");
        return new SymbolSolverCollectionStrategy()
                .collect(Paths.get(projectDir));
    }

    /**
     * Create the symbol solver
     * that will be used to identify
     * user-defined classes
     */
    private static void createSymbolSolver(String projectDir) {
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File(projectDir));
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(javaParserTypeSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration
                .setSymbolResolver(symbolSolver)
                .setAttributeComments(false).setDetectOriginalLineSeparator(true);
        StaticJavaParser
                .setConfiguration(parserConfiguration);
    }

    /**
     * Creates the file set (add appropriate classes)
     *
     * @param sourceRoots the source roots of project
     * @return size of the file set (int)
     */
    private int createFileSet(List<SourceRoot> sourceRoots) {
        try {
            sourceRoots
                    .forEach(sourceRoot -> {
                        try {
                            sourceRoot.tryToParse()
                                    .stream()
                                    .filter(res -> res.getResult().isPresent())
                                    .filter(cu -> cu.getResult().get().getStorage().isPresent())
                                    .forEach(cu -> {
                                        try {
                                            project.getJavaFiles().add(new JavaFile(cu.getResult().get().getStorage().get().getPath().toString().replace("\\", "/").replace(project.getClonePath(), "").substring(1),
                                                    cu.getResult().get().findAll(ClassOrInterfaceDeclaration.class)
                                                            .stream()
                                                            .filter(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.getFullyQualifiedName().isPresent())
                                                            .map(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.getFullyQualifiedName().get())
                                                            .map(Class::new)
                                                            .collect(Collectors.toSet())));
                                        } catch (Throwable ignored) {
                                        }
                                    });
                        } catch (Exception ignored) {
                        }
                    });
        } catch (Exception ignored) {
        }
        return project.getJavaFiles().size();
    }

    /**
     * Starts the calculations
     *
     * @param sourceRoots the list of source roots of project
     */
    private void startCalculations(List<SourceRoot> sourceRoots) {
        AtomicInteger progress = new AtomicInteger(1);
        sourceRoots
                .forEach(sourceRoot -> {
                    System.out.print("\rAnalysing Source Root: " + sourceRoot.getRoot().toString() + " (" + progress + "/" + sourceRoots.size() + ")...");
                    try {
                        sourceRoot.tryToParse()
                                .stream()
                                .filter(res -> res.getResult().isPresent())
                                .forEach(res -> {
                                    analyzeCompilationUnit(res.getResult().get());
                                });
                    } catch (Exception ignored) {
                    }
                    progress.getAndIncrement();
                });
        System.out.println();
    }

    /**
     * Analyzes the compilation unit given.
     *
     * @param cu the compilation unit given
     */
    private void analyzeCompilationUnit(CompilationUnit cu) {
        analyzeClassOrInterfaces(cu);
        analyzeEnums(cu);
    }

    /**
     * Analyzes the classes (or interfaces) given a compilation unit.
     *
     * @param cu the compilation unit given
     */
    private void analyzeClassOrInterfaces(CompilationUnit cu) {
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cl -> {
            try {
                cl.accept(new ClassVisitor(project.getJavaFiles(), cu.getStorage().get().getPath().toString().replace("\\", "/").replace(project.getClonePath(), "").substring(1), cl), null);
            } catch (Exception ignored) {
            }
        });
    }

    /**
     * Analyzes the enumerations given a compilation unit.
     *
     * @param cu the compilation unit given
     */
    private void analyzeEnums(CompilationUnit cu) {
        cu.findAll(EnumDeclaration.class).forEach(cl -> {
            try {
                cl.accept(new ClassVisitor(project.getJavaFiles(), cu.getStorage().get().getPath().toString().replace("\\", "/").replace(project.getClonePath(), "").substring(1), cl), null);
            } catch (Exception ignored) {
            }
        });
    }

    public Project getProject() {
        return project;
    }
}
