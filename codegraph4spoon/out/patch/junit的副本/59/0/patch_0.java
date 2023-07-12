@Test
public void preferRecentlyFailed() {
    Request request = Request.aClass(TwoTests.class);
    MaxCore max = MaxCore.createFresh();
    sortedLeavesForTest();
    List<Description> tests = max.sort(request);
    Description dontSucceed = Description.createTestDescription(TwoTests.class, "dontSucceed");
    assertEquals(dontSucceed, tests.get(0));
}