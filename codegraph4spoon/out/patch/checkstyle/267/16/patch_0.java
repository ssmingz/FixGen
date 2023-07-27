class PlaceHold {
  private void notifyVisit(DetailAST ast, AstState astState) {
    Collection<Check> visitors;
    final String tokenType = Utils.getTokenName(ast.getType());
    if (astState == AstState.WITH_COMMENTS) {
      if (!tokenToCommentChecks.containsKey(tokenType)) {
        return;
      }
      visitors = tokenToCommentChecks.get(tokenType);
    } else {
      if (!tokenToOrdinaryChecks.containsKey(tokenType)) {
        return;
      }
      visitors = tokenToOrdinaryChecks.get(tokenType);
    }
    for (Check c : visitors) {
      c.visitToken(ast);
    }
  }
}
