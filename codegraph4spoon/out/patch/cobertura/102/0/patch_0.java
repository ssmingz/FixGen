private void addInstrumentationToSingleClass(File file) throws Throwable {
    logger.debug("Instrumenting class " + file.getAbsolutePath());
    InputStream inputStream = null;
    ClassWriter cw;
    ClassInstrumenter cv;
    try {
        inputStream = new FileInputStream(file);
        ClassReader cr = new ClassReader(inputStream);
        cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cv = new ClassInstrumenter(projectData, cw, ignoreRegexes, ignoreBranchesRegexes, ignoreTrivial);
        cr.accept(cv, 0);
    } catch (Throwable t) {
        logger.warn("Unable to instrument file " + file.getAbsolutePath(), t);
        return;
    } finally {
        inputStream = IOUtil.closeInputStream(inputStream);
    }
    OutputStream outputStream = null;
    try {
        if (cv.isInstrumented()) {
            File outputFile;
            if (destinationDirectory == null) {
                outputFile = file;
            } else {
                outputFile = new File(destinationDirectory, cv.getClassName().replace('.', separatorChar) + ".class");
            }
            File parentFile = outputFile.getParentFile();
            if (parentFile != null) {
                parentFile.mkdirs();
            }
            byte[] instrumentedClass = cw.toByteArray();
            outputStream = new FileOutputStream(outputFile);
            outputStream.write(instrumentedClass);
        }
    } catch (Throwable t) {
        logger.warn("Unable to instrument file " + file.getAbsolutePath(), t);
        return;
    } finally {
        outputStream = IOUtil.closeOutputStream(outputStream);
    }
}