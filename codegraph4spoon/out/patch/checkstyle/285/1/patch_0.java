class PlaceHold {
  public static String[] getLines(String aFileName, String aCharsetName) throws IOException {
    final List<String> lines = Lists.newArrayList();
    final FileInputStream fr = new FileInputStream(aFileName);
    LineNumberReader lnr = null;
    try {
      lnr = new LineNumberReader(new InputStreamReader(fr, aCharsetName));
    } catch (final UnsupportedEncodingException ex) {
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
      try {
        lnr.close();
      } catch (final IOException e) {
      }
    }
    return lines.toArray(new String[0]);
  }
}
