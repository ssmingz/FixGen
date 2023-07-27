class PlaceHold{
static DetailAST checkOrderSuggestedByJLS(List<DetailAST> modifiers) {
    final Iterator<DetailAST> it = modifiers.iterator();
    DetailAST modifier;
    do {
        modifier = it.next();
    } while ( );
    if (modifier.getType() == TokenTypes.ANNOTATION) {
        return null;
    }
    int i = 0;
    while (modifier != null) {
        if (modifier.getType() == TokenTypes.ANNOTATION) {
            return modifier;
        }
        while ((i < JLS_ORDER.length) && (!JLS_ORDER[i].equals(modifier.getText()))) {
            i++;
        } 
        if (i == JLS_ORDER.length) {
            return modifier;
        } else if (it.hasNext()) {
            modifier = it.next();
        } else {
            modifier = null;
        }
    } 
    return modifier;
}
}