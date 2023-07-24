private ParameterSupplier buildParameterSupplierFromClass(Class<? extends ParameterSupplier> cls) throws Exception {
    Constructor<?>[] supplierConstructors = cls.getConstructors();
    for (Constructor<?> constructor : supplierConstructors) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        if ((parameterTypes.length == 1) && parameterTypes[0].equals(TestClass.class)) {
            return ((ParameterSupplier) (clazz.newInstance()));
        }
    }
    return cls.newInstance();
}