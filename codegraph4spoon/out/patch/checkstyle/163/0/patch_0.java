class PlaceHold {
  public int suggestedChildLevel(ExpressionHandler aChild) {
    DetailAST first = ((DetailAST) (getMainAst().getFirstChild()));
    int indentLevel = getLineStart(first);
    if (aChild instanceof MethodCallHandler) {
      if (!areOnSameLine(
          ((DetailAST) (aChild.getMainAst().getFirstChild())),
          ((DetailAST) (getMainAst().getFirstChild())))) {
        indentLevel += getIndentCheck().getBasicOffset();
      }
    }
    return indentLevel;
  }
}
