class PlaceHold {
  protected void buildMap(
      File fromDir, File toDir, String[] names, FileNameMapper mapper, Hashtable map) {
    String[] toCopy = null;
    if (forceOverwrite) {
      toCopy = names;
    } else {
      SourceFileScanner ds = new SourceFileScanner();
      toCopy = ds.restrict(names, fromDir, toDir, mapper);
    }
    for (int i = 0; i < toCopy.length; i++) {
      File src = new File(fromDir, toCopy[i]);
      File dest = new File(toDir, mapper.mapFileName(toCopy[i])[0]);
      map.put(src.getAbsolutePath(), dest.getAbsolutePath());
    }
  }
}
