@Override
protected boolean matchesSafely(T item) {
    return matcher.matches(item.getMessage());
}