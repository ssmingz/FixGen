class PlaceHold {
  private String[] getOutStreamLines() throws IOException {
    final byte[] bytes = outStream.toByteArray();
    final ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
    final List<String> lineList = new ArrayList<String>();
    while (true) {
      final String line = reader.readLine();
      if (line == null) {
        break;
      }
      lineList.add(line);
    }
    reader.close();
    return lineList.toArray(new String[lineList.size()]);
  }
}
