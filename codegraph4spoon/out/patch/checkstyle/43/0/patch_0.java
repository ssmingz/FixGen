class PlaceHold {
  private void visitLiteralThrows(DetailAST ast) {
    final int count = (ast.getChildCount() + 1) / 2;
    if (count > getMax()) {
      log(ast.getLineNo(), ast.getColumnNo(), count, getMax());
    }
  }
}
