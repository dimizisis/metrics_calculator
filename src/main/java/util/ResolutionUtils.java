package util;

import com.github.javaparser.ast.type.Type;
import lombok.experimental.UtilityClass;

import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import java.util.Optional;

@UtilityClass
public final class ResolutionUtils {

    public static Optional<String> resolveTypeName(Type t) {
        try {
            return Optional.of(t.resolve().describe());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<ResolvedMethodDeclaration> resolveMethod(MethodCallExpr c) {
        try {
            return Optional.of(c.resolve());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<String> resolveClassName(TypeDeclaration<?> d) {
        try {
            return Optional.of(d.resolve().getQualifiedName());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
