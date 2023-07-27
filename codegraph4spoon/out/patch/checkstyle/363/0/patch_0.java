class PlaceHold {
  public static List<String> getTypeParameterNames(final DetailAST aNode) {
    final DetailAST typeParameters = aNode.findFirstToken(TYPE_PARAMETERS);
    final List<String> typeParamNames = new ArrayList<String>();
    if (typeParameters != null) {
      final DetailAST typeParam = typeParameters.findFirstToken(TYPE_PARAMETER);
      typeParamNames.add(typeParam.findFirstToken(IDENT).getText());
      DetailAST sibling = ((DetailAST) (typeParam.getNextSibling()));
      while (sibling != null) {
        if (sibling.getType() == TokenTypes.TYPE_PARAMETER) {
          typeParamNames.add(sibling.findFirstToken(IDENT).getText());
        }
        sibling = ((DetailAST) (sibling.getNextSibling()));
      }
    }
    return typeParamNames;
  }
}
