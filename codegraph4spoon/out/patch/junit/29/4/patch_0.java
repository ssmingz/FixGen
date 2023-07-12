private Object[] computeParams() throws Exception {
    try {
        return getTestClass().get(fParameterSetNumber);
    } catch (ClassCastException e) {
        throw new Exception(String.format("%s.%s() must return a Collection of arrays.", getName(), getParametersMethod().getName()));
    }
}