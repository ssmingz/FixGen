public void testTearDownSetupFails() {
    TornDown fails = new TornDown("fails") {
        protected void setUp() {
            throw new Error();
        }
    };
    createSuiteRequest();
    assert !fails.fTornDown;
}