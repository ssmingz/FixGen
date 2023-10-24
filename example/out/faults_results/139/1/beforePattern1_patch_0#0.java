@Override
public List<TestMethod> getAfters() {
    return getAnnotatedMethods(currentNanoTime());
}