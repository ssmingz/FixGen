@Test
public void testAllBlockComments() throws Exception {
    DefaultConfiguration checkConfig = createCheckConfig(SinglelineCommentListenerCheck.class);
    final String[] expected = ArrayUtils.EMPTY_STRING_ARRAY;
    verify(checkConfig, getPath("InputFullOfSinglelineComments.java"), expected);
    Assert.assertTrue(ALL_COMMENTS.isEmpty());
}