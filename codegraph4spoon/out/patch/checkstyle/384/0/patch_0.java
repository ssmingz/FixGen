class PlaceHold {
  private void checkUnusedTypeParamTags(
      final List<JavadocTag> tags, final List<String> typeParamNames) {
    final Pattern pattern = Pattern.compile("\\s*<([^>]+)>.*");
    for (int i = tags.size() - 1; i >= 0; i--) {
      final JavadocTag tag = tags.get(i);
      if (tag.isParamTag()) {
        final Matcher matcher = pattern.matcher(tag.getFirstArg());
        matcher.find();
        final String typeParamName = matcher.group(1).trim();
        if (!typeParamNames.contains(typeParamName)) {
          log(
              tag.getLineNo(),
              tag.getColumnNo(),
              UNUSED_TAG,
              PARAM.getText(),
              ("<" + typeParamName) + ">");
        }
      }
    }
  }
}
