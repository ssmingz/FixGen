class PlaceHold {
  private void process(File aFile) {
    final String fileName = aFile.getPath();
    final long timestamp = aFile.lastModified();
    if (mCache.alreadyChecked(fileName, timestamp)) {
      return;
    }
    try {
      getMessageDispatcher().fireFileStarted(fileName);
      final String[] lines = Utils.getLines(fileName, getCharset());
      final FileContents contents = new FileContents(fileName, lines);
      final DetailAST rootAST = TreeWalker.parse(contents);
      walk(rootAST, contents);
    } catch (FileNotFoundException fnfe) {
      Utils.getExceptionLogger().debug("FileNotFoundException occured.", fnfe);
      getMessageCollector()
          .add(
              new LocalizedMessage(
                  0, Defn.CHECKSTYLE_BUNDLE, "general.fileNotFound", null, this.getClass()));
    } catch (IOException ioe) {
      Utils.getExceptionLogger().debug("IOException occured.", ioe);
      getMessageCollector()
          .add(
              new LocalizedMessage(
                  0,
                  Defn.CHECKSTYLE_BUNDLE,
                  "general.exception",
                  new String[] {ioe.getMessage()},
                  this.getClass()));
    } catch (RecognitionException re) {
      Utils.getExceptionLogger().debug("RecognitionException occured.", re);
      getMessageCollector()
          .add(
              new LocalizedMessage(
                  re.getLine(),
                  re.getColumn(),
                  Defn.CHECKSTYLE_BUNDLE,
                  "general.exception",
                  new String[] {re.getMessage()},
                  this.getClass()));
    } catch (TokenStreamRecognitionException tre) {
      Utils.getExceptionLogger().debug("TokenStreamRecognitionException occured.", tre);
      final RecognitionException re = tre.recog;
      if (re != null) {
        getMessageCollector()
            .add(
                new LocalizedMessage(
                    re.getLine(),
                    re.getColumn(),
                    Defn.CHECKSTYLE_BUNDLE,
                    "general.exception",
                    new String[] {re.getMessage()},
                    this.getClass()));
      } else {
        getMessageCollector()
            .add(
                new LocalizedMessage(
                    0,
                    Defn.CHECKSTYLE_BUNDLE,
                    "general.exception",
                    new String[] {re.getMessage()},
                    this.getClass()));
      }
    } catch (TokenStreamException te) {
      Utils.getExceptionLogger().debug("TokenStreamException occured.", te);
      getMessageCollector()
          .add(
              new LocalizedMessage(
                  0,
                  Defn.CHECKSTYLE_BUNDLE,
                  "general.exception",
                  new String[] {te.getMessage()},
                  this.getClass()));
    } catch (Throwable err) {
      Utils.getExceptionLogger().debug("Throwable occured.", err);
      getMessageCollector()
          .add(
              new LocalizedMessage(
                  0,
                  Defn.CHECKSTYLE_BUNDLE,
                  "general.exception",
                  new String[] {"" + err},
                  this.getClass()));
    }
    if (getMessageCollector().size() == 0) {
      mCache.checkedOk(fileName, timestamp);
    } else {
      fireErrors(fileName);
    }
    getMessageDispatcher().fireFileFinished(fileName);
  }
}
