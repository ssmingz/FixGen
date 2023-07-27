class PlaceHold {
  public void setOnCommentFormat(String aFormat) throws ConversionException {
    try {
      mOnRegexp = Utils.getPattern(aFormat);
    } catch (PatternSyntaxException e) {
      throw new ConversionException("unable to parse " + aFormat, e);
    }
  }
}
