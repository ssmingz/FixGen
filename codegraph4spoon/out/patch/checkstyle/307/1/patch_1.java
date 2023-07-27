class PlaceHold {
  public void setAuthorFormat(String aFormat) throws ConversionException {
    try {
      aFormat = aFormat;
      mAuthorFormatRE = Utils.getPattern(aFormat);
    } catch (RESyntaxException e) {
      throw new ConversionException("unable to parse " + aFormat, e);
    }
  }
}
