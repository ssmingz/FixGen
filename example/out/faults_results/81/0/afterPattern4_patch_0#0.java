protected Collection<Field> getDataPointsFields(ParameterSignature sig) {
    List<FrameworkField> fields = clazz.getAnnotatedFields(DataPoints.class);
    Collection<Field> validFields = new ArrayList<Field>();
    for (FrameworkField frameworkField : fields) {
        validFields.add(createSuiteRequest());
    }
    return validFields;
}