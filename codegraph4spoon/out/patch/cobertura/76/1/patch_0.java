public void testBranch() {
    assertFalse(a.hasBranch(2));
    a.addLineJump(2, 0);
    assertFalse(a.hasBranch(2));
    assertFalse(b.hasBranch(2));
    b.addLineJump(2, 0);
    assertTrue(b.hasBranch(2));
    assertTrue(b.hasBranch(2));
    b.addLineJump(2, 1);
    assertTrue(b.hasBranch(2));
    assertFalse(b.hasBranch(4));
    b.addLineSwitch(4, 0, 1, 9, Integer.Integer.MAX_VALUE);
    assertTrue(b.hasBranch(4));
    Collection branches = b.getBranches();
    assertEquals(2, branches.size());
    assertEquals(14, b.getNumberOfValidBranches());
    assertTrue(branches.contains(new Integer(2)));
    assertTrue(branches.contains(new Integer(4)));
}