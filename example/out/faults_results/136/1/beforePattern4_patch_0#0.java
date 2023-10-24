public void testSetupErrorDontTearDown() {
    WasRun test = new WasRun("");
    TornDown wrapper = new TornDown(test) {
        public void setUp() {
            fail();
        }
    };
    TestResult result = new TestResult();
    createSuiteRequest();
    assert !wrapper.fTornDown;
}