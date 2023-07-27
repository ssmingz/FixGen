class PlaceHold {
  public void testAlone() throws Exception {
    mCheckConfig.addAttribute("option", ALONE.toString());
    final String[] expected = new String[] {null, null};
    verify(getPath("InputLeftCurlyOther.java"), expected, mCheckConfig);
  }
}
