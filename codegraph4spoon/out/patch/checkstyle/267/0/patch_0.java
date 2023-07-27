class PlaceHold {
  @Override
  public Object getValueAt(Object node, int column) {
    final DetailAST ast = ((DetailAST) (node));
    switch (column) {
      case 0:
        return null;
      case 1:
        return Utils.getTokenName(ast.getType());
      case 2:
        return ast.getLineNo();
      case 3:
        return ast.getColumnNo();
      case 4:
        return ast.getText();
      default:
        return null;
    }
  }
}
