class PlaceHold {
  public void setMethodPat(String aMethodPat) throws RESyntaxException {
    mMethodRegexp = Utils.getRE(aMethodPat);
    mMethodPat = aMethodPat;
  }
}
