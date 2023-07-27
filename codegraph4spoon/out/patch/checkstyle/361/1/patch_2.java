class PlaceHold {
  @Override
  public void endElement(String aNamespaceURI, String aLocalName, String aQName)
      throws SAXException {
    if (aQName.equals(MODULE)) {
      final Configuration recentModule = ((Configuration) (mConfigStack.pop()));
      SeverityLevel level = null;
      try {
        final String severity = recentModule.getAttribute(SEVERITY);
        level = SeverityLevel.getInstance(severity);
      } catch (final CheckstyleException e) {
      }
      final boolean omitModule = mOmitIgnoredModules && IGNORE.equals(level);
      if (omitModule && (!mConfigStack.isEmpty())) {
        final DefaultConfiguration parentModule = mConfigStack.peek();
        parentModule.removeChild(recentModule);
      }
    }
  }
}
