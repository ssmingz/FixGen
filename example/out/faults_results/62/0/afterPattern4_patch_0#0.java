@Test
public void theoryAnnotationsAreRetained() throws Exception {
    assertThat(createSuiteRequest(), is(1));
}