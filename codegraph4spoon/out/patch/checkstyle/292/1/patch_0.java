class PlaceHold {
  public void testWhitespaceCastParenOff() throws Exception {
    mConfig.setBooleanFlag(IGNORE_CAST_WHITESPACE_PROP, null);
    mConfig.setParenPadOption(IGNORE);
    mConfig.setTryBlock(IGNORE);
    mConfig.setCatchBlock(IGNORE);
    final Checker c = createChecker();
    final String filepath = getPath("InputWhitespace.java");
    assertNotNull(c);
    final String[] expected =
        new String[] {
          filepath + ":5:12: '.' is preceeded with whitespace.",
          filepath + ":5:14: '.' is followed by whitespace.",
          filepath + ":13: type Javadoc comment is missing an @author tag.",
          filepath + ":16:22: '=' is not preceeded with whitespace.",
          filepath + ":16:23: '=' is not followed by whitespace.",
          filepath + ":18:24: '=' is not followed by whitespace.",
          filepath + ":26:14: '=' is not preceeded with whitespace.",
          filepath + ":27:10: '=' is not preceeded with whitespace.",
          filepath + ":27:11: '=' is not followed by whitespace.",
          filepath + ":28:10: '+=' is not preceeded with whitespace.",
          filepath + ":28:12: '+=' is not followed by whitespace.",
          filepath + ":29:13: '-=' is not followed by whitespace.",
          filepath + ":29:14: '-' is followed by whitespace.",
          filepath + ":29:21: '+' is followed by whitespace.",
          filepath + ":30:14: '++' is preceeded with whitespace.",
          filepath + ":30:21: '--' is preceeded with whitespace.",
          filepath + ":31:15: '++' is followed by whitespace.",
          filepath + ":31:22: '--' is followed by whitespace.",
          filepath + ":37:21: 'synchronized' is not followed by whitespace.",
          filepath + ":39:12: 'try' is not followed by whitespace.",
          filepath + ":41:14: 'catch' is not followed by whitespace.",
          filepath + ":58:11: 'if' is not followed by whitespace.",
          filepath + ":59:9: '{' should be on the previous line.",
          filepath + ":63:9: '{' should be on the previous line.",
          filepath + ":75:9: '{' should be on the previous line.",
          filepath + ":76:19: 'return' is not followed by whitespace.",
          filepath + ":79:9: '{' should be on the previous line.",
          filepath + ":97:29: '?' is not preceeded with whitespace.",
          filepath + ":97:30: '?' is not followed by whitespace.",
          filepath + ":97:34: ':' is not preceeded with whitespace.",
          filepath + ":97:35: ':' is not followed by whitespace.",
          filepath + ":98:15: '==' is not preceeded with whitespace.",
          filepath + ":98:17: '==' is not followed by whitespace.",
          filepath + ":104:20: '*' is not followed by whitespace.",
          filepath + ":104:21: '*' is not preceeded with whitespace.",
          filepath + ":111:22: '!' is followed by whitespace.",
          filepath + ":112:23: '~' is followed by whitespace.",
          filepath + ":119:18: '%' is not preceeded with whitespace.",
          filepath + ":120:20: '%' is not followed by whitespace.",
          filepath + ":121:18: '%' is not preceeded with whitespace.",
          filepath + ":121:19: '%' is not followed by whitespace.",
          filepath + ":123:18: '/' is not preceeded with whitespace.",
          filepath + ":124:20: '/' is not followed by whitespace.",
          filepath + ":125:18: '/' is not preceeded with whitespace.",
          filepath + ":125:19: '/' is not followed by whitespace.",
          filepath + ":129:17: '.' is preceeded with whitespace.",
          filepath + ":129:24: '.' is followed by whitespace.",
          filepath + ":136:10: '.' is preceeded with whitespace.",
          filepath + ":136:12: '.' is followed by whitespace.",
          filepath + ":153:15: 'assert' is not followed by whitespace.",
          filepath + ":156:20: ':' is not preceeded with whitespace.",
          filepath + ":156:21: ':' is not followed by whitespace."
        };
    verify(c, filepath, expected);
  }
}
