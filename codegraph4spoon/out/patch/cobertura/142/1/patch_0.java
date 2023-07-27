class SummaryXMLReport {
  public SummaryXMLReport(
      ProjectData projectData,
      File destinationDir,
      FileFinder finder,
      ComplexityCalculator complexity)
      throws IOException {
    File file = new File(destinationDir, "coverage-summary.xml");
    pw = IOUtil.getPrintWriter(file);
    try {
      printHeader();
      println(
          ("<!DOCTYPE coverage SYSTEM \"http://cobertura.sourceforge.net/xml/"
                  + XMLReport.coverageDTD)
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
      println("<packages />");
      decreaseIndentation();
      println("</coverage>");
    } finally {
      pw.close();
    }
  }
}
