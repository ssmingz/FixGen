class PlaceHold{
@Override
protected final boolean mustCheckName(DetailAST ast) {
    final DetailAST modifiersAST = ast.findFirstToken(MODIFIERS);
    final boolean isStatic = modifiersAST.branchContains(LITERAL_STATIC);
    final boolean isFinal = modifiersAST.branchContains(FINAL);
    return ((isStatic && (!isFinal)) && shouldCheckInScope(modifiersAST)) && (!);
}
}