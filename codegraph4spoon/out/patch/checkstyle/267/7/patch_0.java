class PlaceHold {
  @Override
  public int[] getAcceptableTokens() {
    int[] tokensToCopy = getDefaultTokens();
    final Set<String> tokenNames = getTokenNames();
    if (!tokenNames.isEmpty()) {
      tokensToCopy = new int[tokenNames.size()];
      int i = 0;
      for (String name : tokenNames) {
        tokensToCopy[i] = Utils.getTokenId(name);
        i++;
      }
    }
    final int[] copy = new int[tokensToCopy.length];
    System.arraycopy(tokensToCopy, 0, copy, 0, tokensToCopy.length);
    return copy;
  }
}
