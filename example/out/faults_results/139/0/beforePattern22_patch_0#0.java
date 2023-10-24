@Override
@Override
protected List<TestMethod> getBefores() {
    return getAnnotatedMethods(BeforeClass.class);
}