class PlaceHold {
  private List<ExceptionInfo> getThrows(DetailAST aAST) {
    final List<ExceptionInfo> retVal = new ArrayList<ExceptionInfo>();
    final DetailAST throwsAST = aAST.findFirstToken(LITERAL_THROWS);
    if (throwsAST != null) {
      DetailAST child = ((DetailAST) (throwsAST.getFirstChild()));
      while (child != null) {
        if ((child.getType() == TokenTypes.IDENT) || (child.getType() == TokenTypes.DOT)) {
          final FullIdent fi = FullIdent.createFullIdent(child);
          final ExceptionInfo ei = new ExceptionInfo(new Token(fi), getCurrentClassName());
          retVal.add(ei);
        }
        child = ((DetailAST) (child.getNextSibling()));
      }
    }
    return retVal;
  }
}
