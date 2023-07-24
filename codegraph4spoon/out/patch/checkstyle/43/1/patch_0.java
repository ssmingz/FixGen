public void checkCount(DetailAST ast) {
    if (checking && (count > getMax())) {
        log(ast.getLineNo(), ast.getColumnNo(), count, getMax());
    }
}