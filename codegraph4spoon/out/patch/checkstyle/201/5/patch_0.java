class PlaceHold{
private void logIOException(IOException ex, File file) {
    String[] args = null;
    String key = "general.fileNotFound";
    if (!(ex instanceof FileNotFoundException)) {
        args = new String[]{ ex.getMessage() };
        key = "general.exception";
    }
    final LocalizedMessage message = new LocalizedMessage(0, Defn.CHECKSTYLE_BUNDLE, key, args, getId(), this.getClass(), null);
    final SortedSet<LocalizedMessage><LocalizedMessage> messages = Sets.newTreeSet();
    messages.add(message);
    getMessageDispatcher().fireErrors(file.getPath(), messages);
    Utils.getExceptionLogger().debug("IOException occured.", ex);
}
}