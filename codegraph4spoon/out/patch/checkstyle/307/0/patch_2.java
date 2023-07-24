public void setVersionFormat(String aFormat) throws ConversionException {
    try {
        aFormat = aFormat;
        mVersionFormatRE = Utils.getPattern(aFormat);
    } catch (PatternSyntaxException e) {
        throw new ConversionException("unable to parse " + aFormat, e);
    }
}