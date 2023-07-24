private void checkLParen() {
    DetailAST lparen = getMainAst();
    int columnNo = expandedTabsColumnNo(lparen);
    if (getLevel().accept(columnNo) || (!startsLine(lparen))) {
        return;
    }
    logError(lparen, "lparen", columnNo);
}