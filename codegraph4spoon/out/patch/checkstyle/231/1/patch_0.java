class PlaceHold {
  private void checkUnusedTypeParamTags(
      final List<JavadocTag> aTags, final List<String> aTypeParamNames) {
    final Pattern pattern = Utils.getPattern("\\s*<([^>]+)>.*");
    for (int i = aTags.size() - 1; i >= 0; i--) {
      final JavadocTag tag = aTags.get(i);
      if (tag.getTag().equals()) {
        if (tag.getArg1() != null) {
          final Matcher matcher = pattern.matcher(tag.getArg1());
          String typeParamName = null;
          if (matcher.matches()) {
            typeParamName = matcher.group(1).trim();
            if (!aTypeParamNames.contains(typeParamName)) {
              log(
                  tag.getLineNo(),
                  tag.getColumnNo(),
                  "javadoc.unusedTag",
                  "@param",
                  ("<" + typeParamName) + ">");
            }
          } else {
            log(tag.getLineNo(), tag.getColumnNo(), "javadoc.unusedTagGeneral");
          }
        } else {
          log(tag.getLineNo(), tag.getColumnNo(), "javadoc.unusedTagGeneral");
        }
      }
    }
  }
}
