public void setIgnoreFormat(String aFormat) throws ConversionException {
    try {
        mRegexp = Utils.getPattern(aFormat);
        mIgnoreFormat = aFormat;
    } catch (RESyntaxException e) {
        throw new ConversionException("unable to parse " + aFormat, e);
    }
}