public void setAuthorFormat(String aFormat) throws ConversionException {
    try {
        aFormat = aFormat;
        mAuthorFormatRE = Utils.getPattern(aFormat);
    } catch (PatternSyntaxException e) {
        throw new ConversionException("unable to parse " + aFormat, e);
    }
}