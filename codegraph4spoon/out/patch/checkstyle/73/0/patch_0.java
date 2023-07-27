class PlaceHold {
  private Checker createChecker() {
    Checker checker;
    try {
      final Properties props = createOverridingProperties();
      final Configuration config =
          ConfigurationLoader.loadConfiguration(
              configLocation, new PropertiesExpander(props), omitIgnoredModules);
      final DefaultContext context = new DefaultContext();
      final ClassLoader loader = new AntClassLoader(getProject(), classpath);
      context.add("classloader", loader);
      final ClassLoader moduleClassLoader = Checker.class.getClassLoader();
      context.add("moduleClassLoader", moduleClassLoader);
      checker = new Checker();
      checker.contextualize(context);
      checker.configure(config);
    } catch (final CheckstyleException e) {
      throw new BuildException(
          String.format(
              "Unable to create a Checker: " + "configLocation {%s}, classpath {%s}.",
              configLocation, classpath, ROOT),
          e);
    }
    return checker;
  }
}
