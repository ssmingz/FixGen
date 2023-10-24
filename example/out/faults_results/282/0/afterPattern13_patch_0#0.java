protected List<Method> getBefores() {
    return getAnnotatedMethods(BeforeClass.class);
}