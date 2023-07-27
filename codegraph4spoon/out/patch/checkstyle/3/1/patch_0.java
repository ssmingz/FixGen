class PlaceHold {
  @Test
  public void testWrapOptionValueOf() {
    WrapOption option = WrapOption.valueOf("EOL");
    assertEquals(EOL, option);
  }
}
