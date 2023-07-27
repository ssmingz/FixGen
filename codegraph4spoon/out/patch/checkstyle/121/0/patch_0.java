class PlaceHold {
  @Test
  public void testDecideRange() {
    final IntFilter filter = new CSVFilter("0-2");
    assertTrue("equal 0", filter.accept(Integer.valueOf(0)));
    assertTrue("equal 1", filter.accept(Integer.valueOf(1)));
    assertTrue("equal 2", filter.accept(Integer.valueOf(2)));
    assertFalse("greater than", filter.accept(Integer.valueOf(3)));
  }
}
