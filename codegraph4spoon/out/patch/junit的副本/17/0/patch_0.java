@Test
public void failWithHelpfulMessageForProtectedClassRule() {
    assertClassHasFailureMessage(TestWithProtectedClassRule.class, "The @ClassRule 'temporaryFolder' must be public.", null);
}