class PlaceHold {
  public void setOffCommentFormat(String aFormat) throws ConversionException {
    try {
      mOffRegexp = Utils.getPattern(aFormat);
    } catch (PatternSyntaxException e) {
      throw new ConversionException("unable to parse " + aFormat, e);
    }
  }
}
