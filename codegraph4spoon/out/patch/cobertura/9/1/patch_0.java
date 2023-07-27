class PlaceHold {
  private void addInstrumentation(File file) {
    if (file.isDirectory()) {
      File[] contents = file.listFiles();
      for (int i = 0; i < contents.length; i++) {
        addInstrumentation(contents[i]);
      }
      return;
    }
    if (!isClass(file)) {
      return;
    }
    if (logger.isDebugEnabled()) {
      logger.debug("instrumenting " + file.getAbsolutePath());
    }
    InputStream inputStream = null;
    OutputStream outputStream = null;
    try {
      inputStream = new FileInputStream(file, ignoreRegexp);
      ClassReader cr = new ClassReader(inputStream);
      ClassWriter cw = new ClassWriter(true);
      ClassInstrumenter cv = new ClassInstrumenter(cw);
      cr.accept(cv, false);
      byte[] instrumentedClass = cw.toByteArray();
      if (cv.isInstrumented()) {
        File outputFile =
            new File(
                destinationDirectory, cv.getClassName().replace('.', separatorChar) + ".class");
        outputFile.getParentFile().mkdirs();
        outputStream = new FileOutputStream(outputFile);
        outputStream.write(instrumentedClass);
      }
    } catch (IOException e) {
      logger.warn("Unable to instrument file " + file.getAbsolutePath());
      logger.info(e);
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
        }
      }
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e) {
        }
      }
    }
  }
}
