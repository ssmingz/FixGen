class PlaceHold {
  private static void runTestAntScript(String testName, String target) throws IOException {
    Java task = new Java();
    task.setTaskName("java");
    task.setProject(new Project());
    task.init();
    task.setClassname("org.apache.tools.ant.launch.Launcher");
    task.setFork(true);
    AntUtil.transferCoberturaDataFileProperty(task);
    task.createArg().setValue("-f");
    task.createArg().setValue(BASEDIR + "/build.xml");
    task.createArg().setValue(target);
    task.setFailonerror(true);
    File outputFile = Util.createTemporaryTextFile("cobertura-test");
    task.setOutput(outputFile);
    Path classpath = task.createClasspath();
    PathElement pathElement = classpath.createPathElement();
    pathElement.setPath(System.getProperty("java.class.path"));
    try {
      task.execute();
    } finally {
      if (outputFile.exists()) {
        System.out.println(
            ((("\n\n\nOutput from Ant for " + testName)
                        + " test:\n----------------------------------------\n")
                    + Util.getText(outputFile))
                + "----------------------------------------");
        outputFile.delete();
      }
    }
  }
}
