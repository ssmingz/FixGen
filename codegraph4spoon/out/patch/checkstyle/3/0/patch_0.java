class PlaceHold {
  @Test
  public void testBlockOptionValueOf() {
    BlockOption option = BlockOption.valueOf("TEXT");
    assertEquals(TEXT, option);
  }
}
