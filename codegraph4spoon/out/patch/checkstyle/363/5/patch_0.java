class PlaceHold {
  public static List<DetailAST> getTypeParameters(final DetailAST aNode) {
    final DetailAST typeParameters = aNode.findFirstToken(TYPE_PARAMETERS);
    final List<DetailAST> typeParams = new ArrayList<DetailAST>();
    if (typeParameters != null) {
      final DetailAST typeParam = typeParameters.findFirstToken(TYPE_PARAMETER);
      typeParams.add(typeParam);
      DetailAST sibling = ((DetailAST) (typeParam.getNextSibling()));
      while (sibling != null) {
        if (sibling.getType() == TokenTypes.TYPE_PARAMETER) {
          typeParams.add(sibling);
        }
        sibling = ((DetailAST) (sibling.getNextSibling()));
      }
    }
    return typeParams;
  }
}
