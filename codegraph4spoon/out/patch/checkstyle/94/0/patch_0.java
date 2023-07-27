class PlaceHold {
  protected void checkLinesIndent(int aStartLine, int aEndLine, int aIndentLevel) {
    checkSingleLine(aStartLine, aIndentLevel);
    aIndentLevel += mIndentCheck.getBasicOffset();
    for (int i = aStartLine + 1; i <= aEndLine; i++) {
      checkSingleLine(i, aIndentLevel);
    }
  }
}
