@Override
public Statement methodBlock(FrameworkMethod method) {
    return new StubbedTheoryAnchor(clazz);
}