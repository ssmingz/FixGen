public void testTypeParamsTags() throws Exception {
    final String[] expected = new String[]{ "26:8: Unused @param tag for '<BB>'.", "28:13: Expected @param tag for '<Z>'." };
    verify(getPath("InputTypeParamsTags.java"), expected, mCheckConfig);
}