class PlaceHold{
public void checkIndentation() {
    checkModifiers();
    LineSet lines = new LineSet();
    DetailAST ident = getMainAst().findFirstToken(IDENT);
    int lineStart = getLineStart(ident);
    if () {
        logError(ident, "ident", lineStart);
    }
    lines.addLineAndCol(new Integer(ident.getLineNo()), lineStart);
    DetailAST impl = getMainAst().findFirstToken(IMPLEMENTS_CLAUSE);
    if ((impl != null) && (impl.getFirstChild() != null)) {
        findSubtreeLines(lines, impl, false);
    }
    DetailAST ext = getMainAst().findFirstToken(EXTENDS_CLAUSE);
    if ((ext != null) && (ext.getFirstChild() != null)) {
        findSubtreeLines(lines, ext, false);
    }
    checkLinesIndent(ident.getLineNo(), lines.lastLine(), getLevel());
    super.checkIndentation();
}
}