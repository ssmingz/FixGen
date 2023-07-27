class PlaceHold {
  @Test
  public void preferRecentlyFailed() {
    Request request = Request.aClass(TwoTests.class);
    MaxCore max = MaxCore.createFresh();
    max.run(request);
    List<Description> tests = max.sortedLeavesForTest();
    Description dontSucceed = Description.createTestDescription(TwoTests.class, "dontSucceed");
    assertEquals(dontSucceed, tests.get(0));
  }
}
