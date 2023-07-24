public IClass resolveType(SymTabAST expr, Scope location, IClass context, boolean referencePhase) {
    IClass result = null;
    SymTabAST nameNode = ((SymTabAST) (expr.getFirstChild()));
    if (nameNode.getType() == TokenTypes.DOT) {
        result = resolveDottedName(nameNode, location, context, );
    } else {
        result = resolveClassIdent(nameNode, location, context, referencePhase);
    }
    return result;
}