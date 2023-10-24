List<Method> getAfters() {
    return field.getAnnotatedMethods(After.class);
}