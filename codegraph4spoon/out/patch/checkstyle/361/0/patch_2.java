class PlaceHold {
  @Override
  public void startElement(String aNamespaceURI, String aLocalName, String aQName, Attributes aAtts)
      throws SAXException {
    if (aQName.equals(MODULE)) {
      final String name = aAtts.getValue(NAME);
      final DefaultConfiguration conf = new DefaultConfiguration(name);
      if (mConfiguration == null) {
        mConfiguration = conf;
      }
      if (!mConfigStack.isEmpty()) {
        final DefaultConfiguration top = mConfigStack.peek();
        top.addChild(conf);
      }
      mConfigStack.push(conf);
    } else if (aQName.equals(PROPERTY)) {
      final String name = aAtts.getValue(NAME);
      final String value;
      try {
        value =
            replaceProperties(
                aAtts.getValue(VALUE), mOverridePropsResolver, aAtts.getValue(DEFAULT));
      } catch (final CheckstyleException ex) {
        throw new SAXException(ex.getMessage());
      }
      final DefaultConfiguration top = mConfigStack.peek();
      top.addAttribute(name, value);
    }
  }
}
