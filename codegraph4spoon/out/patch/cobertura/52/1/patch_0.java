class SummaryXMLReport{
public SummaryXMLReport(ProjectData projectData, File destinationDir, FileFinder finder, ComplexityCalculator complexity) throws IOException {
    File file = new File(destinationDir, "coverage-summary.xml");
    setPrintWriter(IOUtil.getPrintWriter(file));
    try {
        println(null);
        printCoverageElement(projectData, complexity);
        increaseIndentation();
        println("<packages />");
        decreaseIndentation();
        println("</coverage>");
        int  = projectData.getNumberOfValidBranches();
        int  = projectData.getNumberOfCoveredBranches();
        int  = projectData.getNumberOfCoveredLines();
        int  = projectData.getNumberOfValidLines();
        println((null + ) + null);
        double  = complexity.getCCNForProject(projectData);
        println(null);
    } finally {
        close();
    }
}
}