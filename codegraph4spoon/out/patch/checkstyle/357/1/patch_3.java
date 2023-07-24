protected void processLeft(DetailAST ast) {
    final String line = getLines()[ast.getLineNo() - 1];
    final int after = ast.getColumnNo() + 1;
    if (after < line.length()) {
        if ((PadOption.NOSPACE == getAbstractOption()) && Character.isWhitespace(line.charAt(after))) {
            log(ast.getLineNo(), after, "(");
        } else if (((PadOption.SPACE == getAbstractOption()) && (!Character.isWhitespace(line.charAt(after)))) && (line.charAt(after) != ')')) {
            log(ast.getLineNo(), after, "(");
        }
    }
}