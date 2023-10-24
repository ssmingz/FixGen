@Override
protected List<Method> getAfters() {
    return field.getAnnotatedMethods(After.class);
}