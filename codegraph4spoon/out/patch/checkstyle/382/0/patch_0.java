class PlaceHold {
  protected final void log(int aLineNo, int aColNo, String aKey, Object[] aArgs) {
    final int col = 1 + Utils.lengthExpandedTabs(getLines()[aLineNo - 1], aColNo, getTabWidth());
    mMessages.add(
        new LocalizedMessage(
            aLineNo,
            col,
            getMessageBundle(),
            aKey,
            aArgs,
            getSeverityLevel(),
            this.getClass().getName()));
  }
}
