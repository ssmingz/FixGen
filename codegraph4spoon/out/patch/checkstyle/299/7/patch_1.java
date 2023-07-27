class PlaceHold {
  public void test_generics_3() throws Exception {
    final String[] expected =
        new String[] {
          "6:8: Unused @throws tag for 'RE'.",
          "15:34: Expected @throws tag for 'RE'.",
          "31:13: Expected @param tag for '<NPE>'.",
          "38:12: Unused @throws tag for 'E'.",
          "41:38: Expected @throws tag for 'RuntimeException'.",
          "42:13: Expected @throws tag for 'java.lang.RuntimeException'."
        };
    verify(getPath("javadoc/TestGenerics.java"), expected, mCheckConfig);
  }
}
