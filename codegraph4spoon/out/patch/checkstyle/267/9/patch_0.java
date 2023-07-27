class PlaceHold {
  private Details getDetails(DetailAST ast) {
    boolean shouldCheckLastRcurly = false;
    DetailAST rcurly = null;
    DetailAST lcurly = null;
    DetailAST nextToken = null;
    switch (ast.getType()) {
      case Utils.LITERAL_TRY:
        lcurly = ast.getFirstChild();
        nextToken = lcurly.getNextSibling();
        rcurly = lcurly.getLastChild();
        break;
      case TokenTypes.LITERAL_CATCH:
        nextToken = ast.getNextSibling();
        lcurly = ast.getLastChild();
        rcurly = lcurly.getLastChild();
        if (nextToken == null) {
          shouldCheckLastRcurly = true;
          nextToken = getNextToken(ast);
        }
        break;
      case TokenTypes.LITERAL_IF:
        nextToken = ast.findFirstToken(LITERAL_ELSE);
        if (nextToken != null) {
          lcurly = nextToken.getPreviousSibling();
          rcurly = lcurly.getLastChild();
        } else {
          shouldCheckLastRcurly = true;
          nextToken = getNextToken(ast);
          lcurly = ast.getLastChild();
          rcurly = lcurly.getLastChild();
        }
        break;
      case TokenTypes.LITERAL_ELSE:
      case TokenTypes.LITERAL_FINALLY:
        shouldCheckLastRcurly = true;
        nextToken = getNextToken(ast);
        lcurly = ast.getFirstChild();
        rcurly = lcurly.getLastChild();
        break;
      case TokenTypes.CLASS_DEF:
        final DetailAST child = ast.getLastChild();
        lcurly = child.getFirstChild();
        rcurly = child.getLastChild();
        nextToken = ast;
        break;
      case TokenTypes.CTOR_DEF:
      case TokenTypes.STATIC_INIT:
      case TokenTypes.INSTANCE_INIT:
        lcurly = ast.findFirstToken(SLIST);
        rcurly = lcurly.getLastChild();
        nextToken = ast;
        break;
      case TokenTypes.METHOD_DEF:
      case TokenTypes.LITERAL_FOR:
      case TokenTypes.LITERAL_WHILE:
      case TokenTypes.LITERAL_DO:
        lcurly = ast.findFirstToken(SLIST);
        if (lcurly != null) {
          rcurly = lcurly.getLastChild();
        }
        nextToken = ast;
        break;
      default:
        throw new RuntimeException(
            ("Unexpected token type (" + TokenTypes.getTokenName(ast.getType())) + ")");
    }
    final Details details = new Details();
    details.rcurly = rcurly;
    details.lcurly = lcurly;
    details.nextToken = nextToken;
    details.shouldCheckLastRcurly = shouldCheckLastRcurly;
    return details;
  }
}
