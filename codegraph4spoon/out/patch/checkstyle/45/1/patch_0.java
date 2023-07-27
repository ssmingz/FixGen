class PlaceHold {
  public void setTypePat(String aTypePat) throws RESyntaxException {
    mTypeRegexp = Utils.getRE(aTypePat);
    mTypePat = aTypePat;
  }
}
