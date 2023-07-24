private void checkNonlistChild() {
    DetailAST nonlist = getNonlistChild();
    if (nonlist == null) {
        return;
    }
    checkExpressionSubtree(nonlist, getLevel() + getBasicOffset(), false, false);
    IndentLevel  = new IndentLevel(getLevel(), getBasicOffset());
}