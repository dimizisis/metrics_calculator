package main;

import analysis.ProjectAnalysisBounds;
import calculator.MetricCalculator;
import calculator.impl.cohesion.LcomCalculator;
import calculator.impl.complexity.CyclomaticComplexityCalculator;
import calculator.impl.complexity.DitCalculator;
import calculator.impl.complexity.MpcCalculator;
import calculator.impl.complexity.WmcCalculator;
import calculator.impl.coupling.CboCalculator;
import calculator.impl.size.DscCalculator;
import calculator.impl.size.Size1Calculator;
import calculator.impl.size.Size2Calculator;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.utils.SymbolSolverCollectionStrategy;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import infrastructure.entities.JavaClass;
import infrastructure.entities.JavaFile;
import infrastructure.entities.Project;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import visitors.ClassVisitor;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ProjectMetricsAnalyzer {

    @Getter
    private final Project project;

    private final List<MetricCalculator> calculators; // build once

    private AtomicInteger fileAnalysisProgressPercentage;

    private static final Logger logger = LogManager.getLogger(ProjectMetricsAnalyzer.class);

    public ProjectMetricsAnalyzer(Project project) {
        this.project = project;
        this.fileAnalysisProgressPercentage = new AtomicInteger(1);
        this.calculators = buildCalculators();
    }

    /** *
     * Start the analysis of the project.
     *
     * @return 0 if the analysis was successful, -1 otherwise.
     */
    public int start() {
        ProjectRoot projectRoot = getProjectRoot(project.getClonePath());
        List<SourceRoot> sourceRoots = projectRoot.getSourceRoots();
        try {
            createSymbolSolver(project.getClonePath());
        } catch (IllegalStateException e) {
            return -1;
        }
        if (indexProjectClasses(sourceRoots) == 0) {
            logger.error("No classes could be identified! Exiting...");
            return -1;
        }
        analyzeSourceRoots(sourceRoots);
        aggregateProjectMetrics();
        return 0;
    }

    private void aggregateProjectMetrics() {
        project.getJavaFiles().forEach(JavaFile::aggregateMetrics);
    }

    private ProjectRoot getProjectRoot(String projectDir) {
        logger.info("Collecting source roots...");
        return new SymbolSolverCollectionStrategy().collect(Paths.get(projectDir));
    }

    private static void createSymbolSolver(String projectDir) {
        TypeSolver javaParserTypeSolver = new JavaParserTypeSolver(new File(projectDir));
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(javaParserTypeSolver);
        ParserConfiguration parserConfiguration = new ParserConfiguration();
        parserConfiguration
                .setSymbolResolver(symbolSolver)
                .setAttributeComments(false)
                .setDetectOriginalLineSeparator(true);
        StaticJavaParser
                .setConfiguration(parserConfiguration);
    }

    private int indexProjectClasses(List<SourceRoot> sourceRoots) {
        Path root = Paths.get(project.getClonePath());
        try {
            sourceRoots.forEach(sourceRoot -> {
                try {
                    sourceRoot.tryToParse().stream()
                            .filter(res -> res.getResult().isPresent())
                            .filter(res -> res.getResult().get().getStorage().isPresent())
                            .forEach(res -> {
                                try {
                                    Path absolute = res.getResult().get().getStorage().get().getPath();
                                    String relPath = root.relativize(absolute).toString();
                                    project.getJavaFiles().add(new JavaFile(
                                            relPath,
                                            res.getResult().get().findAll(ClassOrInterfaceDeclaration.class).stream()
                                                    .filter(d -> d.getFullyQualifiedName().isPresent())
                                                    .map(d -> d.getFullyQualifiedName().get())
                                                    .map(JavaClass::new)
                                                    .collect(Collectors.toSet())
                                    ));
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

    private void analyzeSourceRoots(List<SourceRoot> sourceRoots) {
        AtomicInteger srcRootProgress = new AtomicInteger(1);
        AtomicInteger overallFileAnalysisProgress = new AtomicInteger(1);

        sourceRoots.forEach(sourceRoot -> {
            logger.info("Analysing Source Root: {} ({}/{})...",
                    sourceRoot.getRoot().toString(), srcRootProgress.get(), sourceRoots.size());

            AtomicInteger fileAnalysisProgress = new AtomicInteger(1);

            try {
                List<ParseResult<CompilationUnit>> parseResults = sourceRoot.tryToParse();

                parseResults.stream()
                        .filter(res -> res.getResult().isPresent())
                        .forEach(res -> {
                            int filePct = fileAnalysisProgress.getAndIncrement() * 100 / Math.max(1, parseResults.size());
                            logger.info("Analysing Source Root: {} ({}%) ({}/{})...",
                                    sourceRoot.getRoot().toString(), filePct, srcRootProgress.get(), sourceRoots.size());

                            analyzeCompilationUnit(res.getResult().get());

                            int totalPct = overallFileAnalysisProgress.getAndIncrement() * 100 / Math.max(1, getProject().getJavaFiles().size());
                            setOverallProgress(new AtomicInteger(totalPct != 0 ? totalPct : 1));
                        });
            } catch (Exception ignored) {
            }

            srcRootProgress.getAndIncrement();
        });
    }

    private void analyzeCompilationUnit(CompilationUnit cu) {
        if (cu.getStorage().isEmpty()) return;
        analyzeClassOrInterfaces(cu);
        analyzeEnums(cu);
    }

    private List<MetricCalculator> buildCalculators() {
        return List.of(
                new LcomCalculator(),
                new CyclomaticComplexityCalculator(),
                new Size1Calculator(),
                new Size2Calculator(),
                new CboCalculator(),
                new DscCalculator(),
                new WmcCalculator(),
                new DitCalculator(),
                new MpcCalculator()
        );
    }

    private void analyzeClassOrInterfaces(CompilationUnit cu) {
        if (cu.getStorage().isEmpty()) {
            return;
        }
        String rel = Paths.get(project.getClonePath()).relativize(cu.getStorage().get().getPath()).toString();

        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(cl -> {
            try {
                cl.accept(new ClassVisitor(
                        project.getJavaFiles(),
                        new ProjectAnalysisBounds(project.getJavaFiles()),
                        rel,
                        calculators
                ), null);
            } catch (Exception ignored) {
            }
        });
    }

    private void analyzeEnums(CompilationUnit cu) {
        if (cu.getStorage().isEmpty()) return;
        String rel = Paths.get(project.getClonePath()).relativize(cu.getStorage().get().getPath()).toString();

        cu.findAll(EnumDeclaration.class).forEach(en -> {
            try {
                en.accept(new ClassVisitor(
                        project.getJavaFiles(),
                        new ProjectAnalysisBounds(project.getJavaFiles()),
                        rel,
                        calculators
                ), null);
            } catch (Exception ignored) {
            }
        });
    }

    /** *
     * Get the overall progress of the file analysis.
     *
     * @return AtomicInteger representing the overall progress percentage.
     */
    public AtomicInteger getOverallProgress() {
        return fileAnalysisProgressPercentage;
    }

    /** *
     * Set the overall progress of the file analysis.
     *
     * @param fileAnalysisProgressPercentage AtomicInteger representing the overall progress percentage.
     */
    public void setOverallProgress(AtomicInteger fileAnalysisProgressPercentage) {
        this.fileAnalysisProgressPercentage = fileAnalysisProgressPercentage;
    }
}