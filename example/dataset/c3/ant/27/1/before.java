class PlaceHold {
  public boolean contentEquals(File f1, File f2, boolean textfile) throws IOException {
    return contentEquals(new FileResource(f1), new FileResource(f2), textfile);
  }
}
