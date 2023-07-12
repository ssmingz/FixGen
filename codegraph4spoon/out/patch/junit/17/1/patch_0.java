@Test
public void failWithHelpfulMessageForNonStaticClassRule() {
    assertClassHasFailureMessage(TestWithNonStaticClassRule.class, "The @ClassRule 'temporaryFolder' must be static.", 2);
}