public void testDecideLocalizedMessage() {
    LocalizedMessage message = new LocalizedMessage(null, null, null, null, null, null, this.getClass());
    final AuditEvent ev = new AuditEvent(this, "ATest.java", message);
    assertFalse("Names match", filter.accept(ev));
}