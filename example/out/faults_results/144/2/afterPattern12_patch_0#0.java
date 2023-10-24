protected List<Method> getAfters() {
    return getAnnotatedMethods(AfterClass.class);
}