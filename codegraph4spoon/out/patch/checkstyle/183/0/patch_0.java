class PlaceHold{
public int process(List<File> aFiles) {
    fireAuditStarted();
    for (final FileSetCheck fsc : mFileSetChecks) {
        fsc.beginProcessing(mCharset);
    }
    for (final File f : aFiles) {
        final String fileName = f.getAbsolutePath();
        fireFileStarted(fileName);
        final SortedSet<LocalizedMessage><LocalizedMessage> fileMessages = Sets.newTreeSet();
        try {
            final FileText theText = new FileText(f.getAbsoluteFile(), mCharset);
            for (final FileSetCheck fsc : mFileSetChecks) {
                fileMessages.addAll(fsc.process(f, theText));
            }
        } catch (final FileNotFoundException fnfe) {
            Utils.getExceptionLogger().debug("FileNotFoundException occured.", fnfe);
            fileMessages.add(new LocalizedMessage(0, Defn.CHECKSTYLE_BUNDLE, "general.fileNotFound", null, null, this.getClass(), null));
        } catch (final IOException ioe) {
            Utils.getExceptionLogger().debug("IOException occured.", ioe);
            fileMessages.add(new LocalizedMessage(0, Defn.CHECKSTYLE_BUNDLE, "general.exception", new String[]{ ioe.getMessage() }, null, this.getClass(), null));
        }
        fireErrors(fileName, fileMessages);
        fireFileFinished(fileName);
    }
    for (final FileSetCheck fsc : mFileSetChecks) {
        fsc.finishProcessing();
        fsc.destroy();
    }
    final int errorCount = mCounter.getCount();
    fireAuditFinished();
    return errorCount;
}
}