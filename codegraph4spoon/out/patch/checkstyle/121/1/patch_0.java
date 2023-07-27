class PlaceHold {
  @Test
  public void testDecide() {
    final IntFilter filter = new IntRangeFilter(0, 10);
    assertTrue("in range", filter.accept(Integer.valueOf(0)));
    assertTrue("in range", filter.accept(Integer.valueOf(5)));
    assertTrue("in range", filter.accept(Integer.valueOf(10)));
    assertFalse("greater than", filter.accept(Integer.valueOf(11)));
  }
}
