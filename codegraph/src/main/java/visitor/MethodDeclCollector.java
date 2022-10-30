package visitor;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.LinkedList;
import java.util.List;

public class MethodDeclCollector extends ASTVisitor {
    private List<MethodDeclaration> methodDeclarations;

    public MethodDeclCollector() {
    }

    public void init() {
        methodDeclarations = new LinkedList<>();
    }

    public List<MethodDeclaration> getAllMethodDecl() {
        return methodDeclarations;
    }

    public boolean visit(MethodDeclaration node) {
        methodDeclarations.add(node);
        return true;
    }
}
