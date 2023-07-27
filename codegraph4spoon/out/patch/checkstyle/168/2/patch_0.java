class PlaceHold {
  private void notifyVisit(DetailAST aAST) {
    final ArrayList visitors = ((ArrayList) (mTokenToChecks.get(getTokenName(aAST.getType()))));
    if (visitors != null) {
      final Map ctx = new HashMap();
      for (int i = 0; i < visitors.size(); i++) {
        final Check check = ((Check) (visitors.get(i)));
        check.setTokenContext(ctx);
        check.visitToken(aAST);
      }
    }
  }
}
