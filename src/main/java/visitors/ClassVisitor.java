package visitors;

import analysis.AnalysisBounds;
import calculator.perclass.ClassMetricCalculator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import context.ClassContext;
import infrastructure.entities.JavaClass;
import infrastructure.entities.JavaFile;
import infrastructure.metrics.QualityMetrics;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import lombok.AllArgsConstructor;
import util.ResolutionUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
public class ClassVisitor extends VoidVisitorAdapter<Void> {

    private final Set<JavaFile> javaFiles;
    private final AnalysisBounds bounds;
    private final String filePath;
    private final List<ClassMetricCalculator> calculators;

    @Override
    public void visit(ClassOrInterfaceDeclaration d, Void arg) {
        analyze(d);
    }

    @Override
    public void visit(EnumDeclaration d, Void arg) {
        analyze(d);
    }

    private void analyze(TypeDeclaration<?> node) {
        if (!belongsToThisFile()) {
            return;
        }

        Optional<String> qnOpt = ResolutionUtils.resolveClassName(node);
        if (qnOpt.isEmpty()) {
            return;
        }

        Optional<JavaClass> javaClassOptional = findJavaClass(qnOpt.get());
        if (javaClassOptional.isEmpty()) {
            return;
        }

        JavaClass javaClass = javaClassOptional.get();

        var ctx = new ClassContext(node, bounds);

        javaClass.setClassContext(ctx);

        node.getMethods().forEach(m -> {
            ctx.startMethod(m);

            ResolutionUtils.resolveTypeName(m.getType()).ifPresent(t -> addEfferentIfInBounds(ctx, t));

            m.getParameters().forEach(p -> ResolutionUtils.resolveTypeName(p.getType()).ifPresent(t -> addEfferentIfInBounds(ctx, t)));

            try {
                m.resolve().getSpecifiedExceptions().forEach(ex -> addEfferentIfInBounds(ctx, ex.describe()));
            } catch (Exception ignored) { }

            m.findAll(NameExpr.class).forEach(ne -> {
                if (ctx.getOwnerFieldNames().contains(ne.getNameAsString())) {
                    ctx.noteFieldAccess(ne.getNameAsString());
                }
            });

            m.findAll(MethodCallExpr.class).forEach(call ->
                    ResolutionUtils.resolveMethod(call).ifPresent(r -> {
                        ctx.getMethodsCalled().add(r.getQualifiedSignature());
                        addEfferentIfInBounds(ctx, r.getPackageName() + "." + r.getClassName());
                    })
            );
        });

        // super types
        if (node.isClassOrInterfaceDeclaration()) {
            node.asClassOrInterfaceDeclaration()
                    .getExtendedTypes()
                    .forEach(et ->
                            ResolutionUtils.resolveTypeName(et).ifPresent(parent -> {
                                // inheritance fact (for NOCC, DIT, etc.)
                                ctx.addDirectParent(parent);

                                // coupling fact (for CBO-like metrics)
                                addEfferentIfInBounds(ctx, parent);
                            })
                    );
        }

        QualityMetrics qm = javaClass.getQualityMetrics();
        calculators.forEach(c -> c.compute(ctx, qm));
    }

    private boolean belongsToThisFile() {
        return javaFiles.stream().anyMatch(f -> f.getPath().equals(filePath));
    }

    private Optional<JavaClass> findJavaClass(String qualifiedName) {
        return javaFiles.stream()
                .filter(f -> f.getPath().equals(filePath))
                .findFirst()
                .flatMap(f -> f.getClasses().stream()
                        .filter(c -> qualifiedName.equals(c.getQualifiedName()))
                        .findFirst());
    }

    private void addEfferentIfInBounds(ClassContext ctx, String qname) {
        if (qname != null && !isSelf(ctx, qname) && bounds.contains(qname)) {
            ctx.getEfferent().add(qname);
        }
    }

    private boolean isSelf(ClassContext ctx, String qname) {
        if (qname == null) {
            return false;
        }
        var classNameOptional = ResolutionUtils.resolveClassName(ctx.getDecl());
        return classNameOptional.filter(qname::equals).isPresent();
    }

}
