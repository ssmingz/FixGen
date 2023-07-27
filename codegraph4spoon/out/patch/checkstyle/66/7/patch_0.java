class PlaceHold {
  public void setIgnoreClassName(String aFormat) throws ConversionException {
    try {
      mIgnoreClassNameRegexp = Utils.getPattern(aFormat);
    } catch (RESyntaxException e) {
      throw new ConversionException("unable to parse " + aFormat, e);
    }
  }
}
