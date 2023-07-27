class PlaceHold {
  public void setParamPat(String aParamPat) throws RESyntaxException {
    mParamRegexp = Utils.getRE(aParamPat);
    mParamPat = aParamPat;
  }
}
