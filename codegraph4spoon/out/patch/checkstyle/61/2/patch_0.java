class PlaceHold {
  private Set<Object> loadKeys(File file) {
    final Set<Object> keys = Sets.newHashSet();
    InputStream inStream = null;
    try {
      inStream = new FileInputStream(file);
      final Properties props = new Properties();
      props.load(inStream);
      final Enumeration<?> e = props.propertyNames();
      while (e.hasMoreElements()) {
        keys.add(e.nextElement());
      }
    } catch (final IOException e) {
      logIOException(e, file);
    } finally {
      Closeables.closeQuietly(inStream);
    }
    return keys;
  }
}
