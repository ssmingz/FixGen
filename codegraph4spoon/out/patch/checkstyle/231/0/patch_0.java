class PlaceHold {
  private void checkTypeParamTag(
      final int aLineNo, final List<JavadocTag> aTags, final String aTypeParamName) {
    boolean found = false;
    for (int i = aTags.size() - 1; i >= 0; i--) {
      final JavadocTag tag = aTags.get(i);
      if ((tag.getTag().equals() && (tag.getArg1() != null))
          && (tag.getArg1().indexOf(("<" + aTypeParamName) + ">") == 0)) {
        found = true;
      }
    }
    if (!found) {
      log(aLineNo, "type.missingTag", ("@param <" + aTypeParamName) + ">");
    }
  }
}
