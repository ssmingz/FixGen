class PlaceHold {
  public void setFileLastModified(File file, long time) {
    ResourceUtils.setLastModified(new FileResource(file), time);
  }
}
