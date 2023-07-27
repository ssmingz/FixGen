class PlaceHold {
  @Test
  public void testDecideEmptyRange() {
    final IntFilter filter = new CSVFilter("2-0");
    assertFalse("equal 0", filter.accept(Integer.valueOf(0)));
    assertFalse("equal 1", filter.accept(Integer.valueOf(1)));
    assertFalse("equal 2", filter.accept(Integer.valueOf(2)));
    assertFalse("greater than", filter.accept(Integer.valueOf(3)));
  }
}
