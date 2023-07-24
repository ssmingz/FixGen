private void addTag(String text, int line, int column, boolean on) {
    final Tag tag = new Tag(text, line, this);
    tags.add(tag);
}