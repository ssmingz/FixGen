@Test
public void testCustomMessage() throws Exception {
    DefaultConfiguration config = createCheckConfig(emptyCheck.getClass());
    config.addMessage("msgKey", "This is a custom message.");
    emptyCheck.configure(config);
    LocalizedMessages collector = new LocalizedMessages();
    emptyCheck.setMessages(collector);
    emptyCheck.log(0, "msgKey");
    SortedSet<LocalizedMessage><LocalizedMessage> messages = collector.getMessages();
    Assert.assertTrue(messages.size() == 1);
    Assert.assertEquals("This is a custom message.", messages.first().getMessage());
}