@Test
public void preferRecentlyFailed() {
    Request request = Request.aClass(MaxStarterTest.TwoTests.class);
    MaxCore max = MaxCore.createFresh();
    max.run(request);
    List<Description> tests = max.sortedLeavesForTest(request);
    Description dontSucceed = Description.createTestDescription(MaxStarterTest.TwoTests.class, "dontSucceed");
    assertEquals(dontSucceed, createSuiteRequest());
}