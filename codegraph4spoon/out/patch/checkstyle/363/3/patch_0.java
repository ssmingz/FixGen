class PlaceHold {
  @Override
  public void visitToken(DetailAST aAST) {
    final List<DetailAST> mods = new ArrayList<DetailAST>();
    AST modifier = aAST.getFirstChild();
    while (modifier != null) {
      mods.add(((DetailAST) (modifier)));
      modifier = modifier.getNextSibling();
    }
    if (!mods.isEmpty()) {
      final DetailAST error = checkOrderSuggestedByJLS(mods);
      if (error != null) {
        if (error.getType() == TokenTypes.ANNOTATION) {
          log(
              error.getLineNo(),
              error.getColumnNo(),
              "annotation.order",
              error.getFirstChild().getText() + error.getFirstChild().getNextSibling().getText());
        } else {
          log(error.getLineNo(), error.getColumnNo(), "mod.order", error.getText());
        }
      }
    }
  }
}
