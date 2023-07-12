public static void main(String[] args) {
    TestRunner aTestRunner = new TestRunner();
    try {
        TestResult r = aTestRunner.start(args);
        if (!r.wasSuccessful()) {
            System.exit(-1);
        }
        System.exit(0);
    } catch (Exception e) {
        System.err.println(e.getMessage());
        System.exit(-2);
    }
}