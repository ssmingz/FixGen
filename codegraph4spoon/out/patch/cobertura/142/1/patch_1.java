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
      printCoverageElement(projectData, complexity);
      increaseIndentation();
      println("<packages />");
      decreaseIndentation();
      println("</coverage>");
    } finally {
      pw.close();
    }
  }
}
