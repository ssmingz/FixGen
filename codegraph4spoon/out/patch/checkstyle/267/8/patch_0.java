class PlaceHold {
  @Override
  public int[] getAcceptableTokens() {
    final Set<String> tokenNames = getTokenNames();
    final int[] result = new int[tokenNames.size()];
    int i = 0;
    for (final String name : tokenNames) {
      result[i] = Utils.getTokenId(name);
      i++;
    }
    return result;
  }
}
