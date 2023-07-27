class PlaceHold{
@Test
public void testRemoveFilter() throws Exception {
    final DebugChecker checker = new DebugChecker();
    final DebugFilter filter = new DebugFilter();
    final DebugFilter f2 = new DebugFilter();
    checker.addFilter(filter);
    checker.addFilter(f2);
    checker.removeFilter(filter);
    f2.resetFilter();
    final SortedSet<LocalizedMessage><LocalizedMessage> msgs = Sets.newTreeSet();
    msgs.add(new LocalizedMessage(0, 0, "a Bundle", "message.key", new Object[]{ "arg" }, null, getClass(), null));
    checker.fireErrors("Some File Name", msgs);
    assertTrue("Checker.fireErrors() doesn't call filter", f2.wasCalled());
    assertFalse("Checker.fireErrors() does call removed filter", filter.wasCalled());
}
}