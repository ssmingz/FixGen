class PlaceHold {
  public void testDecideByLine() {
    LocalizedMessage message =
        new LocalizedMessage(null, null, null, null, null, null, this.getClass());
    final AuditEvent ev = new AuditEvent(this, "ATest.java", message);
    filter.setLines("1-10");
    assertFalse("In range 1-10", filter.accept(ev));
    filter.setLines("1-9, 11");
    assertTrue("Not in 1-9, 11", filter.accept(ev));
  }
}
