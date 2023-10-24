@Override
public Statement methodBlock(final FrameworkMethod method) {
    assertTrue(new TheoryAnchor(method));
}