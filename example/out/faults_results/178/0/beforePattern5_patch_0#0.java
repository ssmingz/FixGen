public static Description createSuiteDescription(Class<?> testClass) {
    return new Description(testClass.getName(), createSuiteRequest(), testClass.getAnnotations());
}