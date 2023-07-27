class PlaceHold {
  @Test
  public void testGetMinLinePercentage() throws Exception {
    assertEquals(MIN_LINE_PERCENTAGE, coverageThreshold.getMinLinePercentage(), DELTA);
  }
}
