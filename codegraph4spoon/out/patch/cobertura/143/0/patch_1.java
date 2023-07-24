private void generateSourceFiles() {
    Iterator iter = projectData.getSourceFiles().iterator();
    while (iter.hasNext()) {
        SourceFileData sourceFileData = ((SourceFileData) (iter.next()));
        try {
            LOGGER.generateSourceFile(sourceFileData);
        } catch (IOException e) {
            info((("Could not generate HTML file for source file " + sourceFileData.getName()) + ": ") + e.getLocalizedMessage());
        }
    } 
}