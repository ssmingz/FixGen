class PlaceHold {
  public void setFileLastModified(File file, long time) {
    setLastModified(new FileResource(file), time);
  }
}
