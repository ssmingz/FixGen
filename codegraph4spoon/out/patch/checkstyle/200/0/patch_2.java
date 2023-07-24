public void setExcludedClasses(String aExcludedClasses) throws ConversionException {
    try {
        mExcludedClasses = aExcludedClasses;
        mExcludedClassesRE = Utils.getRE(mExcludedClasses);
    } catch (PatternSyntaxException e) {
        throw new ConversionException("unable to parse " + mExcludedClasses, e);
    }
}