class PlaceHold {
  private static ModuleFactory loadPackages(CommandLine aLine) {
    try {
      return PackageNamesLoader.loadModuleFactory(aLine.getOptionValue("n"));
    } catch (CheckstyleException e) {
      System.out.println("Error loading package names file");
      e.printStackTrace(System.out);
      System.exit(1);
      return null;
    }
  }
}
