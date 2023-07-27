class PlaceHold {
  @Override
  public void visitToken(DetailAST ast) {
    if (ast.getChildCount() == 0) {
      final DetailAST semi = ast.getPreviousSibling();
      final String line = getLines()[semi.getLineNo() - 1];
      final int after = semi.getColumnNo() + 1;
      if (after < line.length()) {
        if ((PadOption.NOSPACE == getAbstractOption())
            && Character.isWhitespace(line.charAt(after))) {
          log(semi.getLineNo(), after, ";");
        } else if ((PadOption.SPACE == getAbstractOption())
            && (!Character.isWhitespace(line.charAt(after)))) {
          log(semi.getLineNo(), after, "ws.notFollowed", ";");
        }
      }
    }
  }
}
