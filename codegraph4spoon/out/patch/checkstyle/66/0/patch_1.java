class PlaceHold {
  public void setCheckFormat(String aFormat) throws ConversionException {
    try {
      mCheckRegexp = Utils.getPattern(aFormat);
      mCheckFormat = aFormat;
    } catch (PatternSyntaxException e) {
      throw new ConversionException("unable to parse " + aFormat, e);
    }
  }
}
