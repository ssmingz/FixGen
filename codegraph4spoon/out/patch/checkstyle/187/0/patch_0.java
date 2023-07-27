class Configuration{
public Configuration(Properties aProps, PrintStream aLog) throws RESyntaxException, FileNotFoundException, IOException {
    setParamPat(aProps.getProperty(PARAMETER_PATTERN_PROP, PARAMETER_PATTERN));
    setStaticPat(aProps.getProperty(STATIC_PATTERN_PROP, STATIC_PATTERN));
    setStaticFinalPat(aProps.getProperty(CONST_PATTERN_PROP, CONST_PATTERN));
    setMemberPat(aProps.getProperty(MEMBER_PATTERN_PROP, MEMBER_PATTERN));
    setPublicMemberPat(aProps.getProperty(PUBLIC_MEMBER_PATTERN_PROP, PUBLIC_MEMBER_PATTERN));
    setTypePat(aProps.getProperty(TYPE_PATTERN_PROP, TYPE_PATTERN));
    setLocalVarPat(aProps.getProperty(LOCAL_VAR_PATTERN_PROP, LOCAL_VAR_PATTERN));
    setMethodPat(aProps.getProperty(METHOD_PATTERN_PROP, METHOD_PATTERN));
    setMaxLineLength(getIntProperty(aProps, aLog, MAX_LINE_LENGTH_PROP, MAX_LINE_LENGTH));
    setMaxMethodLength(getIntProperty(aProps, aLog, MAX_METHOD_LENGTH_PROP, MAX_METHOD_LENGTH));
    setMaxConstructorLength(getIntProperty(aProps, aLog, MAX_CONSTRUCTOR_LENGTH_PROP, MAX_CONSTRUCTOR_LENGTH));
    setMaxFileLength(getIntProperty(aProps, aLog, MAX_FILE_LENGTH_PROP, MAX_FILE_LENGTH));
    setAllowTabs(getBooleanProperty(aProps, ALLOW_TABS_PROP, mAllowTabs));
    setAllowProtected(getBooleanProperty(aProps, ALLOW_PROTECTED_PROP, mAllowProtected));
    setAllowPackage(getBooleanProperty(aProps, ALLOW_PACKAGE_PROP, mAllowPackage));
    setAllowNoAuthor(getBooleanProperty(aProps, ALLOW_NO_AUTHOR_PROP, mAllowNoAuthor));
    setJavadocScope(Scope.getInstance(aProps.getProperty(JAVADOC_CHECKSCOPE_PROP, PRIVATE.getName())));
    setRequirePackageHtml(getBooleanProperty(aProps, REQUIRE_PACKAGE_HTML_PROP, mRequirePackageHtml));
    setIgnoreImports(getBooleanProperty(aProps, IGNORE_IMPORTS_PROP, mIgnoreImports));
    setIgnoreWhitespace(getBooleanProperty(aProps, IGNORE_WHITESPACE_PROP, mIgnoreWhitespace));
    setIgnoreCastWhitespace(getBooleanProperty(aProps, IGNORE_CAST_WHITESPACE_PROP, mIgnoreCastWhitespace));
    setIgnoreBraces(getBooleanProperty(aProps, IGNORE_BRACES_PROP, mIgnoreBraces));
    setCacheFile(aProps.getProperty(CACHE_FILE_PROP));
    setIgnoreImportLength(getBooleanProperty(aProps, IGNORE_IMPORT_LENGTH_PROP, mIgnoreImportLength));
    setHeaderIgnoreLines(aProps.getProperty(HEADER_IGNORE_LINE_PROP));
    setHeaderLinesRegexp(getBooleanProperty(aProps, HEADER_LINES_REGEXP_PROP, mHeaderLinesRegexp));
    final String fname = aProps.getProperty(HEADER_FILE_PROP);
    if (fname != null) {
        setHeaderFile(fname);
    }
    setLCurlyMethod(getLeftCurlyOptionProperty(aProps, LCURLY_METHOD_PROP, EOL, aLog));
    setLCurlyType(getLeftCurlyOptionProperty(aProps, LCURLY_TYPE_PROP, EOL, aLog));
    (getLeftCurlyOptionProperty(aProps, , EOL, aLog));
}
}