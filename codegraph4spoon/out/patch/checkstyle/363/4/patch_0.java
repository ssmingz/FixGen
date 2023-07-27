class PlaceHold {
  private List<DetailAST> getParameters(DetailAST aAST) {
    final DetailAST params = aAST.findFirstToken(PARAMETERS);
    final List<DetailAST> retVal = new ArrayList<DetailAST>();
    DetailAST child = ((DetailAST) (params.getFirstChild()));
    while (child != null) {
      if (child.getType() == TokenTypes.PARAMETER_DEF) {
        final DetailAST ident = child.findFirstToken(IDENT);
        retVal.add(ident);
      }
      child = ((DetailAST) (child.getNextSibling()));
    }
    return retVal;
  }
}
