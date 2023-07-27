class PlaceHold {
  public ByteBuffer getBytes() throws IOException {
    if (file == null) {
      return null;
    }
    if (file.length() > Integer.MAX_VALUE) {
      throw new IOException("File too large.");
    }
    byte[] bytes = new byte[((int) (file.length())) + 1];
    final FileInputStream stream = new FileInputStream(file);
    try {
      int fill = 0;
      while (true) {
        if (fill >= bytes.length) {
          final byte[] newBytes = new byte[(bytes.length * 2) + 1];
          System.arraycopy(bytes, 0, newBytes, 0, fill);
          bytes = newBytes;
        }
        final int len = stream.read(bytes, fill, bytes.length - fill);
        if (len == (-1)) {
          break;
        }
        fill += len;
      }
      return ByteBuffer.wrap(bytes, 0, fill).asReadOnlyBuffer();
    } finally {
      Closeables.closeQuietly(stream);
    }
  }
}
