public void addFailure(Throwable targetException) {
    if (targetException instanceof MultipleFailureException) {
        addMultipleFailureException(((MultipleFailureException) (targetException)));
    } else {
        fNotifier.fireTestFailure(new Failure(fDescription, targetException));
    }
}