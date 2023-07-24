public void test_generics_2() throws Exception {
    checkConfig.addAttribute("allowThrowsTagsForSubclasses", "true");
    final String[] expected = new String[]{ "15:34: Expected @throws tag for 'RE'.", "23:37: Expected @throws tag for 'RE'.", "31:13: Expected @param tag for '<NPE>'.", "38:12: Unused @throws tag for 'E'.", "41:38: Expected @throws tag for 'RuntimeException'.", "42:13: Expected @throws tag for 'java.lang.RuntimeException'." };
    verify(checkConfig, getPath("javadoc/TestGenerics.java"), expected);
}