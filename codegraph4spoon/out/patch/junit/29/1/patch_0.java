public Object createTest() throws Exception {
    return fTestClass.getConstructor().newInstance();
}