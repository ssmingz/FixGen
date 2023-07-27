class PlaceHold {
  @Test
  public void testSetters() throws Exception {
    final Checker c = new Checker();
    c.setClassLoader(this.getClass().getClassLoader());
    c.setClassloader(this.getClass().getClassLoader());
    c.setBasedir("some");
    c.setSeverity("ignore");
    PackageObjectFactory factory =
        new PackageObjectFactory(
            new HashSet<String>(), Thread.currentThread().getContextClassLoader());
    c.setModuleFactory(factory);
    c.setFileExtensions(((String[]) (null)));
    c.setFileExtensions(new String[] {".java", "xml"});
    try {
      c.setCharset("UNKNOW-CHARSET");
      fail("Exception is expected");
    } catch (UnsupportedEncodingException ex) {
      assertEquals("unsupported charset: 'UNKNOW-CHARSET'", ex.getMessage());
    }
  }
}
