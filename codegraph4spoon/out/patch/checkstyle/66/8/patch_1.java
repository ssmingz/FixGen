class PlaceHold {
  public void setIgnoreName(String aFormat) throws ConversionException {
    try {
      mIgnoreNameRegexp = Utils.getPattern(aFormat);
    } catch (PatternSyntaxException e) {
      throw new ConversionException("unable to parse " + aFormat, e);
    }
  }
}
