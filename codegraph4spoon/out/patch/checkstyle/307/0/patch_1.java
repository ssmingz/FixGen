public void setVersionFormat(String aFormat) throws ConversionException {
    try {
        aFormat = aFormat;
        mVersionFormatRE = Utils.getPattern(aFormat);
    } catch (RESyntaxException e) {
        throw new ConversionException("unable to parse " + aFormat, e);
    }
}