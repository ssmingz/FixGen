class PlaceHold {
  private void parseArguments(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-d")) {
        destinationDirectory = new File(args[++i]);
      } else if (args[i].equals("-basedir")) {
        baseDir = new File(args[++i]);
      } else if (args[i].equals("-ignore")) {
        ignoreRegex = args[++i];
      } else {
        addInstrumentation(args[i]);
      }
    }
  }
}
