public void checkIndentation() {
    checkLabel();
    DetailAST parent = ((DetailAST) (getMainAst().getFirstChild().getNextSibling()));
    checkExpressionSubtree(parent, getLevel() + getBasicOffset(), true, false);
    IndentLevel  = new IndentLevel(getLevel(), getBasicOffset());
}