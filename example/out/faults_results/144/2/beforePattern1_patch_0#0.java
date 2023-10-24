List<Method> getAfters() {
    return getAnnotatedMethods(currentNanoTime());
}