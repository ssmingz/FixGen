public List<TestMethod> getAfters() {
    return getAnnotatedMethods(AfterClass.class);
}