public static Description createSuiteDescription(Class<?> testClass) {
    return new Description("4.5", null, testClass.getAnnotations());
}