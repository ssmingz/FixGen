public void testAlone() throws Exception {
    checkConfig.addAttribute("option", ALONE.toString());
    final String[] expected = new String[]{  };
    verify(checkConfig, getPath("InputLeftCurlyOther.java"), expected);
}