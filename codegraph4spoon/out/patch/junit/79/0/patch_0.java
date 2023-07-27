class PlaceHold {
  @Test
  public void twoTestsNotRunComeBackInRandomOrder() {
    Request request = Request.aClass(TwoTests.class);
    MaxCore max = MaxCore.createFresh();
    List<Description> things = max.sortedLeavesForTest(request);
    Description succeed = Description.createTestDescription(TwoTests.class, "succeed");
    Description dontSucceed = Description.createTestDescription(TwoTests.class, "dontSucceed");
    assertTrue(things.contains(succeed));
    assertTrue(things.contains(dontSucceed));
    assertEquals(2, things.size());
  }
}
