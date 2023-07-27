class PlaceHold {
  private void registerCheck(Check check) throws CheckstyleException {
    final int[] tokens;
    final Set<String> checkTokens = check.getTokenNames();
    if (!checkTokens.isEmpty()) {
      tokens = check.getRequiredTokens();
      final int[] acceptableTokens = check.getAcceptableTokens();
      Arrays.sort(acceptableTokens);
      for (String token : checkTokens) {
        final int tokenId = Utils.getTokenId(token);
        if (Arrays.binarySearch(acceptableTokens, tokenId) >= 0) {
          registerCheck(token, check);
        } else {
          throw new CheckstyleException(
              ((("Token \"" + token) + "\" was not found in Acceptable tokens list") + " in check ")
                  + check);
        }
      }
    } else {
      tokens = check.getDefaultTokens();
    }
    for (int element : tokens) {
      registerCheck(element, check);
    }
    if (check.isCommentNodesRequired()) {
      commentChecks.add(check);
    } else {
      ordinaryChecks.add(check);
    }
  }
}
