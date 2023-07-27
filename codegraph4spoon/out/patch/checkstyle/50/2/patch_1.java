class PlaceHold {
  protected void fireAuditFinished() {
    final AuditEvent evt = new AuditEvent(this);
    final Iterator it = mListeners.iterator();
    while (it.hasNext()) {
      final AuditListener listener = ((AuditListener) (it.next()));
      listener.auditFinished(evt);
    }
  }
}
