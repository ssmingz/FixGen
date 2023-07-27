class PlaceHold {
  private static boolean isSkipCase(DetailAST ast) {
    if (ScopeUtils.isLocalVariableDef(ast) || ScopeUtils.isInInterfaceOrAnnotationBlock(ast)) {
      return true;
    }
    final DetailAST assign = ast.findFirstToken(ASSIGN);
    if (assign == null) {
      return true;
    }
    final DetailAST modifiers = ast.findFirstToken(MODIFIERS);
    return modifiers.branchContains(FINAL);
  }
}
