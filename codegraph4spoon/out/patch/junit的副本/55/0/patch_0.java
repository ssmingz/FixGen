public void testTearDownSetupFails() {
    TornDown fails = new TornDown("fails") {
        protected void setUp() {
            throw new Error();
        }
    };
    verifyError(fails);
    this.assertTrue(fails.fTornDown);
}