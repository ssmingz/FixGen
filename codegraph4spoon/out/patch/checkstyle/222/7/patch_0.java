class PlaceHold {
  public void setPublicMemberPat(String aPublicMemberPat) throws RESyntaxException {
    mPublicMemberRegexp = Utils.getRE(aPublicMemberPat);
    mPublicMemberPat = aPublicMemberPat;
  }
}
