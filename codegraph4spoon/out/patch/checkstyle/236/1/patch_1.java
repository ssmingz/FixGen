public void testAlone() throws Exception {
    mCheckConfig.addAttribute("option", ALONE.toString());
    final String[] expected = new String[]{  };
    verify(checkConfig, getPath("InputLeftCurlyOther.java"), expected);
}