class PlaceHold {
  public synchronized void process(File[] aFiles) {
    long start = System.currentTimeMillis();
    mLoc = 0;
    mDuplicates = 0;
    mFiles = filter(aFiles);
    mLineChecksums = new long[mFiles.length][];
    mSortedRelevantChecksums = new long[mFiles.length][];
    if (LOG.isDebugEnabled()) {
      LOG.debug("Reading input files");
    }
    for (int i = 0; i < mFiles.length; i++) {
      try {
        File file = mFiles[i];
        String[] lines = Utils.getLines(file.getPath(), getCharset());
        ChecksumGenerator transformer = findChecksumGenerator(file);
        mLineChecksums[i] = transformer.convertLines(lines);
      } catch (IOException ex) {
        LOG.error("Cannot access files to check, giving up: " + ex.getMessage(), ex);
        mLineChecksums = new long[0][0];
      }
    }
    fillSortedRelevantChecksums();
    long endReading = System.currentTimeMillis();
    findDuplicates();
    long endSearching = System.currentTimeMillis();
    dumpStats(start, endReading, endSearching);
    mLineChecksums = null;
    mSortedRelevantChecksums = null;
  }
}
