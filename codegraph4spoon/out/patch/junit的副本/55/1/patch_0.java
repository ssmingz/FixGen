public void testRunAndTearDownFails() {
    TornDown fails = new TornDown("fails") {
        protected void tearDown() {
            super.tearDown();
            throw new Error();
            this.assertTrue();
        }

        protected void runTest() {
            throw new Error();
        }
    };
    verifyError(fails);
}