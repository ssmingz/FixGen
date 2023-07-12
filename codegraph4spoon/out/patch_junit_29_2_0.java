protected void collectInitializationErrors(List<Throwable> errors) {
    getTestClass().validateMethodsForDefaultRunner(errors);
}