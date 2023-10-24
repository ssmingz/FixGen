@Override
public Statement methodBlock(FrameworkMethod method) {
    watchedLog.append(new StubbedTheoryAnchor(method));
}