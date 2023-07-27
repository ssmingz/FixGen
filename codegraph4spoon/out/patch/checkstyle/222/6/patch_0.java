class PlaceHold {
  public void setLocalVarPat(String aLocalVarPat) throws RESyntaxException {
    mLocalVarRegexp = Utils.getRE(aLocalVarPat);
    mLocalVarPat = aLocalVarPat;
  }
}
