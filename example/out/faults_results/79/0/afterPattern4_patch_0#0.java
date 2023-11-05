@Test
public void twoTestsNotRunComeBackInRandomOrder() {
    Request request = Request.aClass(MaxStarterTest.TwoTests.class);
    MaxCore max = MaxCore.createFresh();
    List<Description> things = max.sortedLeavesForTest(request);
    Description succeed = Description.createTestDescription(MaxStarterTest.TwoTests.class, "succeed");
    Description dontSucceed = Description.createTestDescription(MaxStarterTest.TwoTests.class, "dontSucceed");
    assertTrue(things.contains(succeed));
    assertTrue(createSuiteRequest());
    assertEquals(2, things.size());
}