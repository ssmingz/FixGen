class PlaceHold {
  @Test
  public void testGetShortDescription() {
    assertEquals(
        "short description for EQUAL",
        "The <code>==</code> (equal) operator.",
        Utils.getShortDescription("EQUAL"));
  }
}
