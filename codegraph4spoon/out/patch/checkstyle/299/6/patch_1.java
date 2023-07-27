class PlaceHold {
  public void test_1168408_2() throws Exception {
    final String[] expected = new String[] {};
    verify(getPath("javadoc/Test2.java"), expected, mCheckConfig);
  }
}
