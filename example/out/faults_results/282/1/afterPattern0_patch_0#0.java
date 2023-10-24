@Override
protected List<Method> getAfters() {
    return getAnnotatedMethods(currentNanoTime());
}