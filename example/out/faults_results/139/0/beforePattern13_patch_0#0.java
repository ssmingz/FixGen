public List<TestMethod> getBefores() {
    return getAnnotatedMethods(BeforeClass.class);
}