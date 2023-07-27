class PlaceHold {
  private void checkSwitchExpr() {
    checkExpressionSubtree(
        getMainAst().findFirstToken(LPAREN).getNextSibling(), getIndent(), false, false);
  }
}
