class PlaceHold {
  public static void main(String[] args) {
    Header.print(System.out);
    boolean hasCommandsFile = false;
    String commandsFileName = null;
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("--commandsfile")) {
        hasCommandsFile = true;
        commandsFileName = args[++i];
      }
    }
    if (hasCommandsFile) {
      List arglist = new ArrayList();
      BufferedReader bufreader = null;
      try {
        bufreader = new BufferedReader(new FileReader(commandsFileName));
        String line;
        while ((line = bufreader.readLine()) != null) {
          arglist.add(line);
        }
      } catch (IOException e) {
        logger.fatal(("Unable to read temporary commands file " + commandsFileName) + ".");
        logger.info(e);
      } finally {
        if (bufreader != null) {
          try {
            bufreader.close();
          } catch (IOException e) {
          }
        }
      }
      args = ((String[]) (arglist.toArray(new String[] {})));
    }
    new Main(args);
  }
}
