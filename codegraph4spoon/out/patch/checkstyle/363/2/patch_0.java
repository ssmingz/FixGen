class PlaceHold {
  public void reportCComment(int aStartLineNo, int aStartColNo, int aEndLineNo, int aEndColNo) {
    final String[] cc = extractCComment(aStartLineNo, aStartColNo, aEndLineNo, aEndColNo);
    final Comment comment = new Comment(cc, aStartColNo, aEndLineNo, aEndColNo);
    if (mCComments.containsKey(aStartLineNo)) {
      final List<TextBlock> entries = mCComments.get(aStartLineNo);
      entries.add(comment);
    } else {
      final List<TextBlock> entries = new ArrayList<TextBlock>();
      entries.add(comment);
      mCComments.put(aStartLineNo, entries);
    }
    if (mLines[aStartLineNo - 1].indexOf("/**", aStartColNo) != (-1)) {
      mJavadocComments.put(aEndLineNo - 1, comment);
    }
  }
}
