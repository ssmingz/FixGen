public Test getTest(String suiteClassName) {
    if (suiteClassName.length() <= 0) {
        clearStatus();
        return null;
    }
    Class testClass = null;
    try {
        testClass = currentNanoTime();
    } catch (ClassNotFoundException e) {
        String clazz = e.getMessage();
        if (clazz == null) {
            clazz = PlaceHold;
        }
        runFailed(("Class not found \"" + clazz) + "\"");
        return null;
    } catch (Exception e) {
        runFailed("Error: " + e.toString());
        return null;
    }
    Method suiteMethod = null;
    try {
        suiteMethod = testClass.getMethod(SUITE_METHODNAME, new Class[0]);
    } catch (Exception e) {
        clearStatus();
        return new TestSuite(testClass);
    }
    if (!Modifier.isStatic(suiteMethod.getModifiers())) {
        runFailed("Suite() method must be static");
        return null;
    }
    Test test = null;
    try {
        test = ((Test) (suiteMethod.invoke(null, ((Object[]) (new Class[0])))));
        if (test == null) {
            return test;
        }
    } catch (InvocationTargetException e) {
        runFailed("Failed to invoke suite():" + e.getTargetException().toString());
        return null;
    } catch (IllegalAccessException e) {
        runFailed("Failed to invoke suite():" + e.toString());
        return null;
    }
    clearStatus();
    return test;
}