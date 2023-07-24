public FileText(File file, String charsetName) throws IOException {
    this.file = file;
    final CharsetDecoder decoder;
    try {
        charset = Charset.forName(charsetName);
        decoder = charset.newDecoder();
        decoder.onMalformedInput(REPLACE);
        decoder.onUnmappableCharacter(REPLACE);
    } catch (final UnsupportedCharsetException ex) {
        final String message = "Unsupported charset: " + charsetName;
        final UnsupportedEncodingException ex2;
        ex2 = new UnsupportedEncodingException(message);
        ex2.initCause(ex);
        throw ex2;
    }
    final char[] chars = new char[READ_BUFFER_SIZE];
    final StringBuilder buf = new StringBuilder();
    final FileInputStream stream = new FileInputStream(file);
    final Reader reader = new InputStreamReader(stream, decoder);
    try {
        while (true) {
            final int len = reader.read(chars);
            if (len < 0) {
                break;
            }
            buf.append(chars, 0, len);
        } 
    } finally {
        Closeables.closeQuietly(reader);
    }
    fullText = buf.toString();
    final ArrayList<String> lines = new ArrayList<>();
    final BufferedReader br = new BufferedReader(new StringReader(fullText));
    for (; ;) {
        final String l = br.readLine();
        if (null == l) {
            break;
        }
        lines.add(l);
    }
    this.lines = lines.toArray(new String[lines.size()]);
}