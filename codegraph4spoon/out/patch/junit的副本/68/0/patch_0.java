public boolean isIgnored() {
    return this.getMethod().getAnnotation(Ignore.class) != null;
}