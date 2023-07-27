class PlaceHold {
  public SortedSet getSourceFiles() {
    SortedSet sourceFiles = new TreeSet();
    Iterator iter = this.children.values().iterator();
    while (iter.hasNext()) {
      PackageData packageData = ((PackageData) (iter.next()));
      sourceFiles.addAll(getSourceFiles());
    }
    return sourceFiles;
  }
}
