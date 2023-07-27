class XMLReport {
  public XMLReport(
      ProjectData projectData,
      File destinationDir,
      FileFinder finder,
      ComplexityCalculator complexity)
      throws IOException {
    this.complexity = complexity;
    this.finder = finder;
    File file = new File(destinationDir, "coverage.xml");
    pw = IOUtil.getPrintWriter(file);
    try {
      printHeader();
      println(
          ("<!DOCTYPE coverage SYSTEM \"http://cobertura.sourceforge.net/xml/" + coverageDTD)
              + "\">");
      println("");
      double ccn = complexity.getCCNForProject(projectData);
      int numLinesCovered = projectData.getNumberOfCoveredLines();
      int numLinesValid = projectData.getNumberOfValidLines();
      int numBranchesCovered = projectData.getNumberOfCoveredBranches();
      int numBranchesValid = projectData.getNumberOfValidBranches();
      println(
          ((((((((((((((((("<coverage line-rate=\"" + projectData.getLineCoverageRate())
                                                                              + "\" branch-rate=\"")
                                                                          + projectData
                                                                              .getBranchCoverageRate())
                                                                      + "\" lines-covered=\"")
                                                                  + numLinesCovered)
                                                              + "\" lines-valid=\"")
                                                          + numLinesValid)
                                                      + "\" branches-covered=\"")
                                                  + numBranchesCovered)
                                              + "\" branches-valid=\"")
                                          + numBranchesValid)
                                      + "\" complexity=\"")
                                  + ccn)
                              + "\" version=\"")
                          + Header.version())
                      + "\" timestamp=\"")
                  + new Date().getTime())
              + "\">");
      increaseIndentation();
      dumpSources();
      dumpPackages(projectData);
      decreaseIndentation();
      println("</coverage>");
    } finally {
      pw.close();
    }
  }
}
