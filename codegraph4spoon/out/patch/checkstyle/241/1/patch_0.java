class PlaceHold {
  @Override
  public void visitToken(DetailAST ast) {
    if (mustCheckName(ast)) {
      final DetailAST nameAST = ast.findFirstToken(IDENT);
      if (!getRegexp().matcher(nameAST.getText()).find()) {
        log(
            nameAST.getLineNo(),
            nameAST.getColumnNo(),
            MSG_INVALID_PATTERN,
            nameAST.getText(),
            getFormat());
      }
    }
  }
}
