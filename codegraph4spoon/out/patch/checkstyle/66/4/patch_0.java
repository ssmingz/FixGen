public void setOnCommentFormat(String aFormat) throws ConversionException {
    try {
        mOnRegexp = Utils.getPattern(aFormat);
    } catch (RESyntaxException e) {
        throw new ConversionException("unable to parse " + aFormat, e);
    }
}