class PlaceHold {
  public void testScopes() throws Exception {
    final String[] expected =
        new String[] {
          "8:5: Missing a Javadoc comment.",
          "9:5: Missing a Javadoc comment.",
          "10:5: Missing a Javadoc comment.",
          "11:5: Missing a Javadoc comment.",
          "19:9: Missing a Javadoc comment.",
          "20:9: Missing a Javadoc comment.",
          "21:9: Missing a Javadoc comment.",
          "22:9: Missing a Javadoc comment.",
          "31:9: Missing a Javadoc comment.",
          "32:9: Missing a Javadoc comment.",
          "33:9: Missing a Javadoc comment.",
          "34:9: Missing a Javadoc comment.",
          "43:9: Missing a Javadoc comment.",
          "44:9: Missing a Javadoc comment.",
          "45:9: Missing a Javadoc comment.",
          "46:9: Missing a Javadoc comment.",
          "56:5: Missing a Javadoc comment.",
          "57:5: Missing a Javadoc comment.",
          "58:5: Missing a Javadoc comment.",
          "59:5: Missing a Javadoc comment.",
          "67:9: Missing a Javadoc comment.",
          "68:9: Missing a Javadoc comment.",
          "69:9: Missing a Javadoc comment.",
          "70:9: Missing a Javadoc comment.",
          "79:9: Missing a Javadoc comment.",
          "80:9: Missing a Javadoc comment.",
          "81:9: Missing a Javadoc comment.",
          "82:9: Missing a Javadoc comment.",
          "91:9: Missing a Javadoc comment.",
          "92:9: Missing a Javadoc comment.",
          "93:9: Missing a Javadoc comment.",
          "94:9: Missing a Javadoc comment.",
          "103:9: Missing a Javadoc comment.",
          "104:9: Missing a Javadoc comment.",
          "105:9: Missing a Javadoc comment.",
          "106:9: Missing a Javadoc comment."
        };
    verify(getPath(("javadoc" + File.separator) + "InputNoJavadoc.java"), expected, mCheckConfig);
  }
}
