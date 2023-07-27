class PlaceHold {
  private void addInstrumentationToSingleClass(File file) {
    logger.debug("Instrumenting class " + file.getAbsolutePath());
    InputStream inputStream = null;
    ClassWriter cw;
    ClassInstrumenter cv;
    try {
      inputStream = new FileInputStream(file);
      ClassReader cr = new ClassReader(inputStream);
      cw = new ClassWriter(true);
      cv = new ClassInstrumenter(this.projectData, cw, this.ignoreRegexs);
      cr.accept(cv, false);
    } catch (Throwable t) {
      logger.warn("Unable to instrument file " + file.getAbsolutePath(), t);
      return;
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
        }
      }
    }
    OutputStream outputStream = null;
    try {
      if (cv.isInstrumented()) {
        File outputFile;
        if (destinationDirectory == null) {
          outputFile = file;
        } else {
          outputFile =
              new File(
                  destinationDirectory, cv.getClassName().replace('.', separatorChar) + ".class");
        }
        File parentFile = outputFile.getParentFile();
        if (parentFile != null) {
          parentFile.mkdirs();
        }
        byte[] instrumentedClass = cw.toByteArray();
        outputStream = new FileOutputStream(outputFile);
        outputStream.write(instrumentedClass);
      }
    } catch (IOException e) {
      logger.warn("Unable to instrument file " + file.getAbsolutePath(), e);
      return;
    } finally {
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e) {
        }
      }
    }
  }
}
