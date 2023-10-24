public static Description createSuiteDescription(Class<?> testClass) {
    return new Description(notifier.getName(), testClass.getAnnotations());
}