class PlaceHold {
  public void test_1168408_1() throws Exception {
    final String[] expected = new String[] {};
    verify(getPath("javadoc/Test1.java"), expected, mCheckConfig);
  }
}
