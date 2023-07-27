class PlaceHold {
  private void addTag(String text, int line) {
    final Tag tag = new Tag(text, line, this);
    tags.add(tag);
  }
}
