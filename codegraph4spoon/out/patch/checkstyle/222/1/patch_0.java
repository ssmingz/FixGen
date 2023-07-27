class PlaceHold {
  public void setTodoPat(String aTodoPat) throws RESyntaxException {
    mTodoRegexp = Utils.getRE(aTodoPat);
    mTodoPat = aTodoPat;
  }
}
