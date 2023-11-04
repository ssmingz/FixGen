class PlaceHold {
  public void copyFile(
      File sourceFile,
      File destFile,
      FilterSetCollection filters,
      Vector filterChains,
      boolean overwrite,
      boolean preserveLastModified,
      String inputEncoding,
      String outputEncoding,
      Project project)
      throws IOException {
    ResourceUtils.copyResource(
        new FileResource(sourceFile),
        new FileResource(destFile),
        filters,
        filterChains,
        overwrite,
        preserveLastModified,
        inputEncoding,
        outputEncoding,
        project);
  }
}
