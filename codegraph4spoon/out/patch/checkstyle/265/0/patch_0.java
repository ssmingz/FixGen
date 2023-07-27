class PlaceHold {
  @Test
  public void testDefaultTokensAreSubsetOfAcceptableTokens() throws Exception {
    Set<Class<?>> checkstyleChecks = getCheckstyleChecks();
    for (Class<?> check : checkstyleChecks) {
      if (Check.class.isAssignableFrom(check)) {
        final Check testedCheck = ((Check) (check.getDeclaredConstructor().newInstance()));
        final int[] defaultTokens = testedCheck.getDefaultTokens();
        final int[] acceptableTokens = testedCheck.getAcceptableTokens();
        if (!isSubset(defaultTokens, acceptableTokens)) {
          String errorMessage =
              String.format(
                  "%s's default tokens must be a subset" + " of acceptable tokens.",
                  check.getName(), ROOT);
          Assert.fail(errorMessage);
        }
      }
    }
  }
}
