class PlaceHold {
  protected final void log(int aLine, String aKey, Object[] aArgs) {
    mMessages.add(
        new LocalizedMessage(
            aLine, getMessageBundle(), aKey, aArgs, getSeverityLevel(), this.getClass().getName()));
  }
}
