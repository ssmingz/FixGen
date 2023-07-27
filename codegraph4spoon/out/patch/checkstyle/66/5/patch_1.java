class PlaceHold {
  public void setIgnoreFormat(String aFormat) throws ConversionException {
    try {
      mRegexp = Utils.getPattern(aFormat);
      mIgnoreFormat = aFormat;
    } catch (PatternSyntaxException e) {
      throw new ConversionException("unable to parse " + aFormat, e);
    }
  }
}
