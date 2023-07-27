class PlaceHold {
  @Test
  public void preferFast() {
    Request request = Request.aClass(TwoUnEqualTests.class);
    MaxCore max = MaxCore.createFresh();
    max.run(request);
    Description thing = get(1);
    assertEquals(Description.createTestDescription(TwoUnEqualTests.class, "slow"), thing);
  }
}
