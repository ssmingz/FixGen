class SummaryXMLReport{
public SummaryXMLReport(ProjectData projectData, File destinationDir, FileFinder finder, ComplexityCalculator complexity) throws IOException {
    File file = new File(destinationDir, "coverage-summary.xml");
    try {
        println(null);
        println((((((((((((((((((null + projectData.getLineCoverageRate()) + null) + projectData.getBranchCoverageRate()) + null) + ) + null) + ) + null) + ) + null) + ) + null) + ) + null) + Header.version()) + null) + new Date().getTime()) + null);
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
        int  = projectData.getNumberOfCoveredBranches();
        int  = projectData.getNumberOfValidBranches();
        int  = projectData.getNumberOfValidLines();
        int  = projectData.getNumberOfCoveredLines();
        double  = complexity.getCCNForProject(projectData);
        println((null + ) + null);
        println(null);
        pw = IOUtil.getPrintWriter(file);
    } finally {
        .close();
    }
}
}