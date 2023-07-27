class PlaceHold {
  public InstrumentationResult instrumentClass(InputStream inputStream) throws IOException {
    ClassReader cr0 = new ClassReader(inputStream);
    ClassWriter cw0 = new ClassWriter(0);
    DetectIgnoredCodeClassVisitor detectIgnoredCv =
        new DetectIgnoredCodeClassVisitor(cw0, ignoreTrivial, ignoreMethodAnnotations);
    DetectDuplicatedCodeClassVisitor cv0 = new DetectDuplicatedCodeClassVisitor(detectIgnoredCv);
    cr0.accept(cv0, 0);
    ClassReader cr = new ClassReader(cw0.toByteArray());
    ClassWriter cw = new ClassWriter(0);
    BuildClassMapClassVisitor cv =
        new BuildClassMapClassVisitor(
            cw,
            ignoreRegexes,
            cv0.getDuplicatesLinesCollector(),
            detectIgnoredCv.getIgnoredMethodNamesAndSignatures());
    cr.accept(cv, EXPAND_FRAMES);
    if (logger.isDebugEnabled()) {
      logger.debug("=============== Detected duplicated code =============");
      Map<Integer, Map<Integer, Integer>> l = cv0.getDuplicatesLinesCollector();
      for (Map.Entry<Integer, Map<Integer, Integer>> m : l.entrySet()) {
        if (m.getValue() != null) {
          for (Map.Entry<Integer, Integer> pair : m.getValue().entrySet()) {
            logger.debug(
                (((((cv.getClassMap().getClassName() + ":") + m.getKey()) + " ") + pair.getKey())
                        + "->")
                    + pair.getValue());
          }
        }
      }
      logger.debug("=============== End of detected duplicated code ======");
    }
    logger.debug(
        "Migrating classmap in projectData to store in *.ser file: "
            + cv.getClassMap().getClassName());
    cv.getClassMap().applyOnProjectData(projectData, cv.shouldBeInstrumented());
    if (cv.shouldBeInstrumented()) {
      ClassReader cr2 = new ClassReader(cw0.toByteArray());
      ClassWriter cw2 = new ClassWriter(ClassWriter.COMPUTE_MAXS);
      cv.getClassMap().assignCounterIds();
      logger.debug(
          (("Assigned " + cv.getClassMap().getMaxCounterId()) + " counters for class:")
              + cv.getClassMap().getClassName());
      InjectCodeClassInstrumenter cv2 =
          new InjectCodeClassInstrumenter(
              cw2,
              ignoreRegexes,
              threadsafeRigorous,
              cv.getClassMap(),
              cv0.getDuplicatesLinesCollector(),
              detectIgnoredCv.getIgnoredMethodNamesAndSignatures());
      cr2.accept(cv2, EXPAND_FRAMES);
      return new InstrumentationResult(cv.getClassMap().getClassName(), cw2.toByteArray());
    } else {
      logger.debug("Class shouldn't be instrumented: " + cv.getClassMap().getClassName());
      return null;
    }
  }
}
