@Override
public void visitToken(DetailAST ast) {
    final String text = ast.getText();
    if (matcher(text).find()) {
        String customMessage = message;
        if (customMessage.isEmpty()) {
            customMessage = MSG_KEY;
        }
        log(ast.getLineNo(), ast.getColumnNo(), customMessage);
    }
}