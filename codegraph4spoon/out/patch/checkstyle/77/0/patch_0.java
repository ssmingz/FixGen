class PlaceHold {
  private void checkNonlistChild() {
    DetailAST nonlist = getNonlistChild();
    if (nonlist == null) {
      return;
    }
    checkExpressionSubtree(nonlist, getLevel() + getBasicOffset(), false, false);
  }
}
