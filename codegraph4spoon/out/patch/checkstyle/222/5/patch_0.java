class PlaceHold {
  public void setStaticFinalPat(String aStaticFinalPat) throws RESyntaxException {
    mStaticFinalRegexp = Utils.getRE(aStaticFinalPat);
    mStaticFinalPat = aStaticFinalPat;
  }
}
