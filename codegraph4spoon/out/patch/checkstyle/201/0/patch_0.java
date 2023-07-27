class PlaceHold{
@Test
public void testCustomMessageWithParameters() throws Exception {
    DefaultConfiguration config = createCheckConfig(emptyCheck.getClass());
    config.addMessage("msgKey", "This is a custom message with {0}.");
    emptyCheck.configure(config);
    LocalizedMessages collector = new LocalizedMessages();
    emptyCheck.setMessages(collector);
    emptyCheck.log(0, "msgKey", "TestParam");
    SortedSet<LocalizedMessage><LocalizedMessage> messages = collector.getMessages();
    Assert.assertTrue(messages.size() == 1);
    Assert.assertEquals("This is a custom message with TestParam.", messages.first().getMessage());
}
}