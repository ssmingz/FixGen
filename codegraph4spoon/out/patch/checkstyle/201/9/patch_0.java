protected final void fireErrors(String fileName) {
    final SortedSet<LocalizedMessage><LocalizedMessage> errors = getMessageCollector().getMessages();
    getMessageCollector().reset();
    getMessageDispatcher().fireErrors(fileName, errors);
}