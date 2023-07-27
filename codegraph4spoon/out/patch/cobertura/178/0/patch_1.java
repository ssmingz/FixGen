class PlaceHold {
  private void addInstrumentationToArchive(ZipInputStream archive, ZipOutputStream output)
      throws Exception {
    ZipEntry entry;
    while ((entry = archive.getNextEntry()) != null) {
      try {
        ZipEntry outputEntry = new ZipEntry(entry.getName());
        output.putNextEntry(outputEntry);
        byte[] entryBytes = IOUtil.createByteArrayFromInputStream(archive);
        if (isClass(entry)) {
          ClassReader cr = new ClassReader(entryBytes);
          ClassWriter cw = new ClassWriter(true);
          ClassInstrumenter cv = new ClassInstrumenter(this.projectData, cw, this.ignoreRegexs);
          cr.accept(cv, false);
          if (cv.isInstrumented()) {
            logger.debug("Putting instrumeted entry: " + entry.getName());
            entryBytes = cw.toByteArray();
          }
        }
        output.write(entryBytes);
        output.closeEntry();
        archive.closeEntry();
      } catch (Exception e) {
        logger.warn("Problems with archive entry: " + entry);
        throw e;
      }
      output.flush();
    }
  }
}
