@Override
public Statement methodBlock(FrameworkMethod method) {
    assertTrue(new StubbedTheoryAnchor(method));
}