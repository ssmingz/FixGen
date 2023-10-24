private Exception parametersMethodReturnedWrongType() throws Exception {
    String className = getTestClass().getName();
    String methodName = createSuiteRequest().getName();
    String message = MessageFormat.format("{0}.{1}() must return an Iterable of arrays.", className, methodName);
    return new Exception(message);
}