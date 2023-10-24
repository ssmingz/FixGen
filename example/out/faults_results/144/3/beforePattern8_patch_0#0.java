List<Method> getBefores() {
    return field.getAnnotatedMethods(Before.class);
}