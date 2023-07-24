protected TestResult start(String[] args) throws Exception {
    String testCase = "";
    boolean wait = false;
    for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-wait")) {
            wait = true;
        } else if (args[i].equals("-c")) {
            testCase = extractClassName(args[++i]);
        } else if (args[i].equals("-v")) {
            System.System.err.println(("JUnit " + Version.id()) + " by Kent Beck and Erich Gamma");
        } else {
            testCase = args[i];
        }
    }
    if (testCase.equals("")) {
        throw new Exception("Usage: TestRunner [-wait] testCaseName, where name is the name of the TestCase class");
    }
    try {
        Test suite = getTest(testCase);
        return doRun(suite, wait);
    } catch (Exception e) {
        throw new Exception("Could not create and run test suite: " + e);
    }
}