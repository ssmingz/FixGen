class PlaceHold {
  private boolean isOverridingMethod(DetailAST ast) {
    if ((ast.getType() != TokenTypes.METHOD_DEF)
        || ScopeUtils.isInInterfaceOrAnnotationBlock(ast)) {
      return false;
    }
    final DetailAST nameAST = ast.findFirstToken(IDENT);
    final String name = nameAST.getText();
    final DetailAST modifiersAST = ast.findFirstToken(MODIFIERS);
    if ((!getMethodName().equals(name)) || modifiersAST.branchContains(LITERAL_NATIVE)) {
      return false;
    }
    final DetailAST params = ast.findFirstToken(PARAMETERS);
    return params.getChildCount() == 0;
  }
}
