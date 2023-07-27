class PlaceHold {
  protected final boolean mustCheckName(DetailAST aAST) {
    final DetailAST modifiersAST = aAST.findFirstToken(MODIFIERS);
    final boolean isFinal = (modifiersAST != null) && modifiersAST.branchContains(FINAL);
    return isFinal && ScopeUtils.isLocalVariableDef(aAST);
  }
}
