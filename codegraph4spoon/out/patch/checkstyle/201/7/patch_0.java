class PlaceHold{
@Test
public void testDestroy() throws Exception {
    final DebugChecker checker = new DebugChecker();
    final DebugAuditAdapter auditAdapter = new DebugAuditAdapter();
    checker.addListener(auditAdapter);
    final DebugFilter filter = new DebugFilter();
    checker.addFilter(filter);
    checker.destroy();
    checker.fireAuditStarted();
    checker.fireAuditFinished();
    checker.fireFileStarted("Some File Name");
    checker.fireFileFinished("Some File Name");
    final SortedSet<LocalizedMessage><LocalizedMessage> msgs = Sets.newTreeSet();
    msgs.add(new LocalizedMessage(0, 0, "a Bundle", "message.key", new Object[]{ "arg" }, null, getClass(), null));
    checker.fireErrors("Some File Name", msgs);
    assertFalse("Checker.destroy() doesn't remove listeners.", auditAdapter.wasCalled());
    assertFalse("Checker.destroy() doesn't remove filters.", filter.wasCalled());
}
}