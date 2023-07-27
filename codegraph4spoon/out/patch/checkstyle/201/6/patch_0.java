class PlaceHold{
@Test
public void testAddFilter() throws Exception {
    final DebugChecker checker = new DebugChecker();
    final DebugFilter filter = new DebugFilter();
    checker.addFilter(filter);
    filter.resetFilter();
    final SortedSet<LocalizedMessage><LocalizedMessage> msgs = Sets.newTreeSet();
    msgs.add(new LocalizedMessage(0, 0, "a Bundle", "message.key", new Object[]{ "arg" }, null, getClass(), null));
    checker.fireErrors("Some File Name", msgs);
    assertTrue("Checker.fireErrors() doesn't call filter", filter.wasCalled());
}
}