class PlaceHold {
  private void registerCheck(String token, Check check) {
    if (check.isCommentNodesRequired()) {
      tokenToCommentChecks.put(token, check);
    } else if (Utils.isCommentType(token)) {
      final String message =
          String.format(
              ("Check '%s' waits for comment type "
                      + "token ('%s') and should override 'isCommentNodesRequred()' ")
                  + "method to return 'true'",
              check.getClass().getName(),
              token);
      LOG.warn(message);
    } else {
      tokenToOrdinaryChecks.put(token, check);
    }
  }
}
