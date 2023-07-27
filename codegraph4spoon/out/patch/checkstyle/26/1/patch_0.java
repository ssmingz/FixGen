class PlaceHold {
  @Override
  public void visitToken(DetailAST ast) {
    if (isInContext(ast, ALLOWED_ASSIGMENT_CONTEXT)) {
      return;
    }
    if (isInNoBraceControlStatement(ast)) {
      return;
    }
    if (isInWhileIdiom(ast)) {
      return;
    }
    log(ast.getLineNo(), ast.getColumnNo(), MSG_KEY);
  }
}
