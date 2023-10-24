@Override
@Override
protected List<TestMethod> getAfters() {
    return getAnnotatedMethods(AfterClass.class);
}