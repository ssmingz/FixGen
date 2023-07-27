class PlaceHold {
  private boolean isCheckedMethod(DetailAST aAST) {
    String methodName = aAST.findFirstToken(IDENT).getText();
    return !mIgnoredMethodNames.contains(methodName);
  }
}
