class PlaceHold {
  @Test
  public void testGetMinBranchPercentage() throws Exception {
    assertEquals(MIN_BRANCH_PERCENTAGE, coverageThreshold.getMinBranchPercentage(), DELTA);
  }
}
