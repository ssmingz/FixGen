class PlaceHold {
  private static Properties loadProperties(File aFile) {
    final Properties properties = new Properties();
    try {
      FileInputStream fis = null;
      fis = new FileInputStream(aFile);
      properties.load(fis);
      fis.close();
    } catch (IOException ex) {
      System.out.println("Unable to load properties from file: " + aFile.getAbsolutePath());
      ex.printStackTrace(System.out);
      System.exit(1);
    }
    return properties;
  }
}
