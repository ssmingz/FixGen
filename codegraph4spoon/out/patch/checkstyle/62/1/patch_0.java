class PlaceHold {
  final void setHeader(String aHeader) {
    if ((aHeader == null) || (aHeader.trim().length() == 0)) {
      return;
    }
    checkHeaderNotInitialized();
    final String headerExpandedNewLines = aHeader.replaceAll("\\\\n", "\n");
    final Reader headerReader = new StringReader(headerExpandedNewLines);
    try {
      loadHeader(headerReader);
    } catch (final IOException ex) {
      throw new ConversionException("unable to load header", ex);
    } finally {
      try {
        headerReader.close();
      } catch (final IOException ex) {
        throw new ConversionException("unable to close header", ex);
      }
    }
  }
}
