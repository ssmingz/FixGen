class PlaceHold {
  public void setMessageFormat(String aFormat) throws ConversionException {
    try {
      Utils.getPattern(aFormat);
    } catch (PatternSyntaxException e) {
      throw new ConversionException("unable to parse " + aFormat, e);
    }
    mMessageFormat = aFormat;
  }
}
