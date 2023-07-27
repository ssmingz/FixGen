class PlaceHold {
  @Deprecated
  public static String[] getLines(String fileName, String charsetName) throws IOException {
    final List<String> lines = Lists.newArrayList();
    final FileInputStream fr = new FileInputStream(fileName);
    LineNumberReader lnr = null;
    try {
      lnr = new LineNumberReader(new InputStreamReader(fr, charsetName));
    } catch (final UnsupportedEncodingException ex) {
      fr.close();
      final String message = "unsupported charset: " + ex.getMessage();
      throw new UnsupportedEncodingException(message);
    }
    try {
      while (true) {
        final String l = lnr.readLine();
        if (l == null) {
          break;
        }
        lines.add(l);
      }
    } finally {
      Closeables.closeQuietly(lnr);
    }
    return lines.toArray(new String[lines.size()]);
  }
}
