class PlaceHold {
  private void visitVariableDef(DetailAST variableDef) {
    final boolean inInterfaceOrAnnotationBlock =
        ScopeUtils.isInInterfaceOrAnnotationBlock(variableDef);
    if ((!inInterfaceOrAnnotationBlock) && (!hasIgnoreAnnotation(variableDef))) {
      final DetailAST varNameAST = variableDef.findFirstToken(TYPE).getNextSibling();
      final String varName = varNameAST.getText();
      if (!hasProperAccessModifier(variableDef, varName)) {
        log(varNameAST.getLineNo(), varNameAST.getColumnNo(), MSG_KEY, varName);
      }
    }
  }
}
