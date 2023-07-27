class PlaceHold {
  public void fireErrors(String aFileName, LocalizedMessage[] aErrors) {
    final String stripped = getStrippedFileName(aFileName);
    for (int i = 0; i < aErrors.length; i++) {
      final AuditEvent evt = new AuditEvent(this, stripped, aErrors[i]);
      final Iterator it = mListeners.iterator();
      while (it.hasNext()) {
        final AuditListener listener = ((AuditListener) (it.next()));
        listener.addError(evt);
      }
    }
  }
}
