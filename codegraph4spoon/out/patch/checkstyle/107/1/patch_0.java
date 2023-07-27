class PlaceHold {
  void checkWSAfter(int aLineNo, int aColNo, MyToken aConstruct, String aAllow) {
    if (mConfig.isIgnoreWhitespace()
        || ((MyToken.CAST == aConstruct) && mConfig.isIgnoreCastWhitespace())) {
      return;
    }
    final String line = mLines[aLineNo - 1];
    if (((aColNo < line.length()) && (!Character.isWhitespace(line.charAt(aColNo))))
        && (aAllow.indexOf(line.charAt(aColNo)) == (-1))) {
      mMessages.add(aLineNo, aColNo, null);
    }
  }
}
