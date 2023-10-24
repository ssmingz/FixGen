@Override
public List<FrameworkMethod> getAfters() {
    return getAnnotatedMethods(currentNanoTime());
}