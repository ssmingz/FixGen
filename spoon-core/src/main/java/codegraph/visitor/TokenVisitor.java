package codegraph.visitor;

import codegraph.CtVirtualElement;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.CtScanner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TokenVisitor extends CtScanner {
    public List<String> tokens = new ArrayList<>();

    @Override
    public <T> void visitCtLiteral(final CtLiteral<T> literal) {
        tokens.add(literal.prettyprint());
        super.visitCtLiteral(literal);
    }

    @Override
    public <T> void visitCtFieldReference(final CtFieldReference<T> reference) {
        tokens.add(reference.prettyprint());
        super.visitCtFieldReference(reference);
    }

    @Override
    public <T> void visitCtThisAccess(final CtThisAccess<T> thisAccess) {
        if (!thisAccess.prettyprint().equals(""))
            tokens.add(thisAccess.prettyprint());
        super.visitCtThisAccess(thisAccess);
    }

    @Override
    public <T> void visitCtExecutableReference(final CtExecutableReference<T> reference) {
        tokens.add(reference.prettyprint());
        super.visitCtExecutableReference(reference);
    }

    @Override
    public <T> void visitCtBinaryOperator(final CtBinaryOperator<T> operator) {
        tokens.add(operator.getKind().name());
        super.visitCtBinaryOperator(operator);
    }

    @Override
    public <T> void visitCtConstructorCall(final CtConstructorCall<T> ctConstructorCall) {
        tokens.add("new");
        super.visitCtConstructorCall(ctConstructorCall);
    }

    @Override
    public void visitCtContinue(final CtContinue continueStatement) {
        tokens.add(continueStatement.prettyprint());
    }

    @Override
    public void visitCtBreak(final CtBreak breakStatement) {
        tokens.add(breakStatement.prettyprint());
    }

    @Override
    public <S> void visitCtCase(final CtCase<S> caseStatement) {
        tokens.add("case");
        super.visitCtCase(caseStatement);
    }

    @Override
    public void visitCtCatch(final CtCatch catchBlock) {
        tokens.add("catch");
        super.visitCtCatch(catchBlock);
    }

    @Override
    public void visitCtDo(final CtDo doLoop) {
        tokens.add("do");
        super.visitCtDo(doLoop);
    }

    @Override
    public void visitCtFor(final CtFor forLoop) {
        tokens.add("for");
        super.visitCtFor(forLoop);
    }

    @Override
    public void visitCtForEach(final CtForEach foreach) {
        tokens.add("for");
        super.visitCtForEach(foreach);
    }

    @Override
    public void visitCtIf(final CtIf ifElement) {
        tokens.add("if");
        super.visitCtIf(ifElement);
    }

    @Override
    public <S> void visitCtSwitch(final CtSwitch<S> switchStatement) {
        tokens.add("switch");
        super.visitCtSwitch(switchStatement);
    }

    @Override
    public void visitCtThrow(final CtThrow throwStatement) {
        tokens.add("throw");
        super.visitCtThrow(throwStatement);
    }

    @Override
    public void visitCtTry(final CtTry tryBlock) {
        tokens.add("try");
        super.visitCtTry(tryBlock);
    }

    @Override
    public void visitCtWhile(final CtWhile whileLoop) {
        tokens.add("while");
        super.visitCtWhile(whileLoop);
    }

    @Override
    public <T> void visitCtAssert(final CtAssert<T> asserted) {
        tokens.add("assert");
        super.visitCtAssert(asserted);
    }

    @Override
    public <T> void visitCtLocalVariable(final CtLocalVariable<T> localVariable) {
        tokens.add(localVariable.getType().getSimpleName());
        tokens.add(localVariable.getSimpleName());
        super.visitCtLocalVariable(localVariable);
    }

    @Override
    public <R> void visitCtReturn(final CtReturn<R> returnStatement) {
        tokens.add("return");
        super.visitCtReturn(returnStatement);
    }

    @Override
    public <T> void visitCtVariableRead(final CtVariableRead<T> variableRead) {
        tokens.add(variableRead.prettyprint());
    }

    @Override
    public <T> void visitCtMethod(final CtMethod<T> m) {
        tokens.addAll(m.getModifiers().stream().map(ModifierKind::toString).collect(Collectors.toList()));
        tokens.add(m.getType().getSimpleName());  // return type
        tokens.add(m.getSimpleName());  // method name
        super.visitCtMethod(m);
    }

    @Override
    public <T> void visitCtParameter(final CtParameter<T> parameter) {
        tokens.add(parameter.getSimpleName());
        tokens.add(parameter.getType().getSimpleName());
    }

}
