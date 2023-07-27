class PlaceHold {
  private void visitMethodDef(DetailAST aAST) {
    String name = aAST.findFirstToken(IDENT).getText();
    if (name.equalsIgnoreCase(SET_UP_METHOD_NAME)) {
      checkSetUpTearDownMethod(aAST, name, SET_UP_METHOD_NAME);
    } else if (name.equalsIgnoreCase(TEAR_DOWN_METHOD_NAME)) {
      checkSetUpTearDownMethod(aAST, name, TEAR_DOWN_METHOD_NAME);
    } else if (name.equalsIgnoreCase(SUITE_METHOD_NAME)) {
      checkSuiteMethod(aAST, name);
    }
  }
}
