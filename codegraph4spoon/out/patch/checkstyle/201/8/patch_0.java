@Test
public void testIOException() throws Exception {
    final UniquePropertiesCheck check = new UniquePropertiesCheck();
    check.configure(checkConfig);
    final String fileName = getPath("InputUniquePropertiesCheckNotExisting.properties");
    final File file = new File(fileName);
    final SortedSet<LocalizedMessage><LocalizedMessage> messages = check.process(file, Collections.<String>emptyList());
    Assert.assertEquals("Wrong messages count: " + messages.size(), messages.size(), 1);
    final LocalizedMessage message = messages.iterator().next();
    final String retrievedMessage = messages.iterator().next().getKey();
    Assert.assertEquals(("Message key '" + retrievedMessage) + "' is not valid", retrievedMessage, "unable.open.cause");
    Assert.assertEquals(("Message '" + message.getMessage()) + "' is not valid", message.getMessage(), getCheckMessage(IO_EXCEPTION_KEY, fileName, getFileNotFoundDetail(file)));
}