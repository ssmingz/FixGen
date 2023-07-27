class PlaceHold {
  private void notifyLeave(DetailAST aAST) {
    final ArrayList visitors = ((ArrayList) (mTokenToChecks.get(getTokenName(aAST.getType()))));
    if (visitors != null) {
      for (int i = 0; i < visitors.size(); i++) {
        final Check check = ((Check) (visitors.get(i)));
        check.leaveToken(aAST);
      }
    }
  }
}
