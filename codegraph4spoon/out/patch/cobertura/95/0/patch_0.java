public static void main(String[] args) {
    Header.print(System.out);
    long startTime = System.currentTimeMillis();
    Main main = new Main();
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
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(commandsFileName));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                arglist.add(line);
            } 
        } catch (IOException e) {
            logger.fatal(("Unable to read temporary commands file " + commandsFileName) + ".");
            logger.info(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
        }
        args = ((String[]) (arglist.toArray(new String[]{  })));
    }
    main.parseArguments(args);
    long stopTime = System.currentTimeMillis();
    System.out.println(("Instrument time: " + (stopTime - startTime)) + "ms");
}