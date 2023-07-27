class PlaceHold {
  public void setStaticPat(String aStaticPat) throws RESyntaxException {
    mStaticRegexp = Utils.getRE(aStaticPat);
    mStaticPat = aStaticPat;
  }
}
