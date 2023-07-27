class PlaceHold {
  protected Collection<Field> getSingleDataPointFields(ParameterSignature sig) {
    List<FrameworkField> fields = clazz.getAnnotatedFields(DataPoint.class);
    Collection<Field> validFields = new ArrayList<Field>();
    for (FrameworkField frameworkField : fields) {
      validFields.add(frameworkField.getField());
    }
    return validFields;
  }
}
