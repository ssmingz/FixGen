public void test_1168408_3() throws Exception {
    mCheckConfig.addAttribute("allowThrowsTagsForSubclasses", "true");
    mCheckConfig.addAttribute("allowUndeclaredRTE", "true");
    final String[] expected = new String[]{  };
    verify(checkConfig, getPath("javadoc/Test3.java"), expected);
}