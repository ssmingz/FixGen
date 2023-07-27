class PlaceHold {
  public void setMemberPat(String aMemberPat) throws RESyntaxException {
    mMemberRegexp = Utils.getRE(aMemberPat);
    mMemberPat = aMemberPat;
  }
}
