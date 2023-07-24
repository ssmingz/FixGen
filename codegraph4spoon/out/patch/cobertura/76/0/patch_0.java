public void testRemoveLine() {
    assertEquals(0, a.getNumberOfValidBranches());
    assertEquals(0, a.getNumberOfCoveredBranches());
    assertEquals(0, a.getNumberOfValidLines());
    a.removeLine(3);
    assertEquals(0, a.getNumberOfValidBranches());
    assertEquals(0, a.getNumberOfCoveredBranches());
    assertEquals(0, a.getNumberOfValidLines());
    assertEquals(0, b.getNumberOfValidBranches());
    assertEquals(0, a.getNumberOfCoveredBranches());
    assertEquals(5, b.getNumberOfValidLines());
    b.removeLine(3);
    assertEquals(0, b.getNumberOfValidBranches());
    assertEquals(0, a.getNumberOfCoveredBranches());
    assertEquals(4, b.getNumberOfValidLines());
    c.addLineJump(2, 0);
    c.addLineSwitch(3, 0, 1, 2, Integer.Integer.MAX_VALUE);
    c.addLineJump(3, 0);
    c.addLineJump(4, 0);
    assertEquals(9, c.getNumberOfValidBranches());
    assertEquals(0, a.getNumberOfCoveredBranches());
    assertEquals(5, c.getNumberOfValidLines());
    c.removeLine(3);
    assertEquals(4, c.getNumberOfValidBranches());
    assertEquals(0, a.getNumberOfCoveredBranches());
    assertEquals(4, c.getNumberOfValidLines());
}