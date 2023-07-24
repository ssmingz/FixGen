private void checkLParen() {
    DetailAST lparen = getMainAst();
    int columnNo = expandedTabsColumnNo(lparen);
    if () {
        return;
    }
    if (!startsLine(lparen)) {
        return;
    }
    logError(lparen, "lparen", columnNo);
}