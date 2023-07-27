class PlaceHold {
  public void testTagsWithSubclassesAllowed() throws Exception {
    mCheckConfig.addAttribute("allowThrowsTagsForSubclasses", "true");
    final String[] expected =
        new String[] {
          "14:5: Missing a Javadoc comment.",
          "18:9: Unused @param tag for 'unused'.",
          "24: Expected an @return tag.",
          "33: Expected an @return tag.",
          "40:16: Expected @throws tag for 'Exception'.",
          "49:16: Expected @throws tag for 'Exception'.",
          "55:16: Expected @throws tag for 'Exception'.",
          "55:27: Expected @throws tag for 'NullPointerException'.",
          "60:22: Expected @param tag for 'aOne'.",
          "68:22: Expected @param tag for 'aOne'.",
          "72:9: Unused @param tag for 'WrongParam'.",
          "73:23: Expected @param tag for 'aOne'.",
          "73:33: Expected @param tag for 'aTwo'.",
          "78:8: Unused @param tag for 'Unneeded'.",
          "79: Unused Javadoc tag.",
          "87:8: Duplicate @return tag.",
          "109:23: Expected @param tag for 'aOne'.",
          "109:55: Expected @param tag for 'aFour'.",
          "109:66: Expected @param tag for 'aFive'.",
          "178:8: Unused @throws tag for 'ThreadDeath'.",
          "179:8: Unused @throws tag for 'ArrayStoreException'.",
          "256:28: Expected @throws tag for 'IOException'.",
          "262:8: Unused @param tag for 'aParam'.",
          "320:9: Missing a Javadoc comment.",
          "329:5: Missing a Javadoc comment.",
          "333: Unused Javadoc tag."
        };
    verify(checkConfig, getPath("InputTags.java"), expected);
  }
}
