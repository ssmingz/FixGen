class PlaceHold {
  private boolean addInstrumentationToArchive(
      CoberturaFile file, ZipInputStream archive, ZipOutputStream output) throws Exception {
    boolean modified = false;
    ZipEntry entry;
    while ((entry = archive.getNextEntry()) != null) {
      try {
        String entryName = entry.getName();
        if (ArchiveUtil.isSignatureFile(entry.getName())) {
          continue;
        }
        ZipEntry outputEntry = new ZipEntry(entry.getName());
        outputEntry.setComment(entry.getComment());
        outputEntry.setExtra(entry.getExtra());
        outputEntry.setTime(entry.getTime());
        output.putNextEntry(outputEntry);
        byte[] entryBytes = IOUtil.createByteArrayFromInputStream(archive);
        if (classPattern.isSpecified() && ArchiveUtil.isArchive(entryName)) {
          Archive archiveObj = new Archive(file, entryBytes);
          addInstrumentationToArchive(archiveObj);
          if (archiveObj.isModified()) {
            modified = true;
            entryBytes = archiveObj.getBytes();
            outputEntry.setTime(System.currentTimeMillis());
          }
        } else if (isClass(entry) && classPattern.matches(entryName)) {
          ClassReader cr = new ClassReader(entryBytes);
          ClassWriter cw = new ClassWriter(true);
          ClassInstrumenter cv =
              new ClassInstrumenter(projectData, cw, ignoreRegexes, ignoreBranchesRegexes);
          cr.accept(cv, false);
          if (cv.isInstrumented()) {
            logger.debug("Putting instrumented entry: " + entry.getName());
            entryBytes = cw.toByteArray();
            modified = true;
            outputEntry.setTime(System.currentTimeMillis());
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
    return modified;
  }
}
