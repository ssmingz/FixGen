@Test
public void testRequiredTokensAreSubsetOfAcceptableTokens() throws Exception {
    Set<Class<?>> checkstyleChecks = getCheckstyleChecks();
    for (Class<?> check : checkstyleChecks) {
        if (Check.class.isAssignableFrom(check)) {
            final Check testedCheck = ((Check) (check.getDeclaredConstructor().newInstance()));
            final int[] requiredTokens = testedCheck.getRequiredTokens();
            final int[] acceptableTokens = testedCheck.getAcceptableTokens();
            if (!isSubset(requiredTokens, acceptableTokens)) {
                String errorMessage = String.format("%s's required tokens must be a subset" + " of acceptable tokens.", check.getName(), ROOT);
                Assert.fail(errorMessage);
            }
        }
    }
}