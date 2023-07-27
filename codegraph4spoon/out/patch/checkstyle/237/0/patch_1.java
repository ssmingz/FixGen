class PlaceHold {
  @Override
  public void visitToken(DetailAST aAST) {
    if (isOverridingMethod(aAST)) {
      mMethodStack.add(new MethodNode(aAST));
    } else if (isSuperCall(aAST)) {
      final MethodNode methodNode = mMethodStack.getLast();
      methodNode.setCallsSuper();
    }
  }
}
