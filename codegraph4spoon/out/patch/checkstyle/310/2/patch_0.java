class PlaceHold {
  private static Configuration loadConfig(CommandLine aLine, Properties aProps) {
    try {
      return ConfigurationLoader.loadConfiguration(
          aLine.getOptionValue("c"), new PropertiesExpander(aProps));
    } catch (CheckstyleException e) {
      System.out.println("Error loading configuration file");
      e.printStackTrace(System.out);
      System.exit(1);
      return null;
    }
  }
}
