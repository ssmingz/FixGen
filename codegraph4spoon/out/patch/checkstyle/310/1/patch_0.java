class PlaceHold {
  private static Checker createChecker(
      Configuration aConfig, ModuleFactory aFactory, AuditListener aNosy) {
    Checker c = null;
    try {
      c = new Checker();
      c.setModuleFactory(aFactory);
      c.configure(aConfig);
      c.addListener(aNosy);
    } catch (Exception e) {
      System.out.println("Unable to create Checker: " + e.getMessage());
      e.printStackTrace(System.out);
      System.exit(1);
    }
    return c;
  }
}
