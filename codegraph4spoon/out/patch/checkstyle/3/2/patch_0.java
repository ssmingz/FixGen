class PlaceHold {
  @Test
  public void testPadOptionValueOf() {
    PadOption option = PadOption.valueOf("NOSPACE");
    assertEquals(NOSPACE, option);
  }
}
