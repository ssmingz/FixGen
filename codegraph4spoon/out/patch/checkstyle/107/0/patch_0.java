class PlaceHold {
  void verifyLongEll(int aLineNo, int aColNo) {
    if ((!mConfig.isIgnoreLongEll()) && (mLines[aLineNo - 1].charAt(aColNo) == 'l')) {
      mMessages.add(aLineNo, aColNo, null);
    }
  }
}
