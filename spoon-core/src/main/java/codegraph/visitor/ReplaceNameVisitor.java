package codegraph.visitor;

import spoon.compiler.Environment;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.reference.*;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.reflect.visitor.PrintingContext;

public class ReplaceNameVisitor extends DefaultJavaPrettyPrinter {

    /**
     * Creates a new code generator visitor.
     *
     * @param env
     */
    public ReplaceNameVisitor(Environment env) {
        super(env);
    }

    @Override
    public <T> void visitCtLiteral(CtLiteral<T> literal) {
        enterCtExpression(literal);
        if (literal.getValue() == null)
            printer.writeLiteral("null");
        else
            printer.writeLiteral(literal.getValue().getClass().getName());
        exitCtExpression(literal);
    }

    @Override
    public <T> void visitCtLocalVariableReference(CtLocalVariableReference<T> reference) {
        printer.writeIdentifier(reference.getType().getQualifiedName());
    }

    @Override
    public <T> void visitCtCatchVariableReference(CtCatchVariableReference<T> reference) {
        printer.writeIdentifier(reference.getType().getQualifiedName());
    }

    @Override
    public <T> void visitCtParameterReference(CtParameterReference<T> reference) {
        printer.writeIdentifier(reference.getType().getQualifiedName());
    }

    @Override
    public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
        enterCtExpression(variableRead);
        printer.writeIdentifier(variableRead.getVariable().getType().getQualifiedName());
        exitCtExpression(variableRead);
    }

    @Override
    public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
        enterCtExpression(variableWrite);
        printer.writeIdentifier(variableWrite.getVariable().getType().getQualifiedName());
        exitCtExpression(variableWrite);
    }

    @Override
    public <T> void visitCtUnboundVariableReference(CtUnboundVariableReference<T> reference) {
        printer.writeIdentifier(reference.getType().getQualifiedName());
    }

    @Override
    public <T> void visitCtFieldReference(CtFieldReference<T> reference) {
        boolean isStatic = "class".equals(reference.getSimpleName()) || !"super".equals(reference.getSimpleName()) && reference.isStatic();

        boolean printType = true;

        if (reference.isFinal() && reference.isStatic()) {
            CtTypeReference<?> declTypeRef = reference.getDeclaringType();
            if (declTypeRef.isAnonymous()) {
                //never print anonymous class ref
                printType = false;
            } else {
                if (context.isInCurrentScope(declTypeRef)) {
                    //do not printType if we are in scope of that type
                    printType = false;
                }
            }
        }

        if (isStatic && printType && !context.ignoreStaticAccess()) {
            try (PrintingContext.Writable _context = context.modify().ignoreGenerics(true)) {
                scan(reference.getDeclaringType());
            }
            printer.writeSeparator(".");
        }
        if ("class".equals(reference.getSimpleName())) {
            printer.writeKeyword("class");
        } else {
            printer.writeIdentifier(reference.getType()!=null?reference.getType().getQualifiedName():"");
        }
    }

}

