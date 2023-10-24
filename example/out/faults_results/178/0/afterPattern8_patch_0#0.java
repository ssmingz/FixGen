public static Description createSuiteDescription(Class<?> testClass) {
    return new Description(testClass.getName(), field.getAnnotations());
}