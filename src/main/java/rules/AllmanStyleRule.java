package rules;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AllmanStyleRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        String src = node.getImage();
        if (!src.contains("{\n")) {
            asCtx(data).addViolation(node);
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        String src = node.getImage();
        if (!src.matches("if\\( .* \\)")) {
            asCtx(data).addViolation(node);
        }
        return super.visit(node, data);
    }
}
