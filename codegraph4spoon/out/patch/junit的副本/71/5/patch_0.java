private ParameterSupplier getSupplier(ParameterSignature unassigned) throws Exception {
    ParametersSuppliedBy annotation = unassigned.findDeepAnnotation(ParametersSuppliedBy.class);
    if (annotation != null) {
        return buildParameterSupplierFromClass(annotation.value());
    } else {
        return new AllMembersSupplier(clazz);
    }
}