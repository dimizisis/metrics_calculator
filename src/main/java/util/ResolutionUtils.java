package util;

import com.github.javaparser.ast.type.Type;
import lombok.experimental.UtilityClass;

import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

import java.util.Optional;

@UtilityClass
public final class ResolutionUtils {

    /** *
     * Resolve the given type and return its name.
     *
     * @param t the type to resolve
     * @return an Optional containing the resolved type name, or empty if resolution fails
     */
    public static Optional<String> resolveTypeName(Type t) {
        try {
            return Optional.of(t.resolve().describe());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /** *
     * Resolve the given method call expression.
     *
     * @param c the method call expression to resolve
     * @return an Optional containing the resolved method declaration, or empty if resolution fails
     */
    public static Optional<ResolvedMethodDeclaration> resolveMethod(MethodCallExpr c) {
        try {
            return Optional.of(c.resolve());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /** *
     * Resolve the given type declaration and return its qualified name.
     *
     * @param d the type declaration to resolve
     * @return an Optional containing the resolved qualified name, or empty if resolution fails
     */
    public static Optional<String> resolveClassName(TypeDeclaration<?> d) {
        try {
            return Optional.of(d.resolve().getQualifiedName());
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
