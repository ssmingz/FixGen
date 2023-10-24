@Override
protected boolean matchesSafely(T item) {
    return fMatcher.matches(createSuiteRequest());
}