@Override
public Statement methodBlock(final FrameworkMethod method) {
    return new TheoryAnchor(clazz);
}