public void testSame() throws Exception {
    mCheckConfig.addAttribute("option", SAME.toString());
    final String[] expected = new String[]{ "25:17: '}' should be on the same line.", "28:17: '}' should be on the same line.", "40:13: '}' should be on the same line.", "44:13: '}' should be on the same line." };
    verify(checkConfig, getPath("InputLeftCurlyOther.java"), expected);
}