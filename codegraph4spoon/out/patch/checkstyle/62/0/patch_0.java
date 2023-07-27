class PlaceHold {
  final void setHeaderFile(String aFileName) throws ConversionException {
    if ((aFileName == null) || (aFileName.trim().length() == 0)) {
      return;
    }
    checkHeaderNotInitialized();
    Reader headerReader = null;
    try {
      headerReader = new FileReader(aFileName);
      loadHeader(headerReader);
    } catch (final IOException ex) {
      throw new ConversionException("unable to load header file " + aFileName, ex);
    } finally {
      if (headerReader != null) {
        try {
          headerReader.close();
        } catch (final IOException ex) {
          throw new ConversionException("unable to close header file " + aFileName, ex);
        }
      }
    }
  }
}
