class PlaceHold{
@Test
public void ignoreExceptionsFromDataPointMethods() {
    assertThat(testResult(.), isSuccessful());
}
}