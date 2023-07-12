public void testTearDownAfterError() {
    TornDown fails = new TornDown("fails");
    verifyError(fails);
    this.assertTrue();
}