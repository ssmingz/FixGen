class PlaceHold {
  private int getConstructorParameterCount() {
    List<ParameterSignature> signatures = ParameterSignature.signatures(clazz.getOnlyConstructor());
    int constructorParameterCount = signatures.size();
    return constructorParameterCount;
  }
}
