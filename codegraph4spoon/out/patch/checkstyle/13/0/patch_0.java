class PlaceHold {
  @Override
  public void visitToken(DetailAST ast) {
    final DetailAST parent = ast.getParent();
    switch (parent.getType()) {
      case TokenTypes.NOT_EQUAL:
      case TokenTypes.EQUAL:
      case TokenTypes.LNOT:
      case TokenTypes.LOR:
      case TokenTypes.LAND:
        log(parent.getLineNo(), parent.getColumnNo());
        break;
      default:
        break;
    }
  }
}
