class XMLReport{
public XMLReport(ProjectData projectData, File destinationDir, FileFinder finder, ComplexityCalculator complexity) throws IOException {
    this.complexity = complexity;
    this.finder = finder;
    File file = new File(destinationDir, "coverage.xml");
    setPrintWriter(IOUtil.getPrintWriter(file));
    try {
        println(null);
        println((((((((((((((((((null + projectData.getLineCoverageRate()) + null) + projectData.getBranchCoverageRate()) + null) + ) + null) + ) + null) + ) + null) + ) + null) + ) + null) + Header.version()) + null) + new Date().getTime()) + null);
        increaseIndentation();
        dumpSources();
        dumpPackages(projectData);
        decreaseIndentation();
        println("</coverage>");
        int  = projectData.getNumberOfValidLines();
        println((null + ) + null);
        int  = projectData.getNumberOfValidBranches();
        int  = projectData.getNumberOfCoveredLines();
        int  = projectData.getNumberOfCoveredBranches();
        println(null);
        double  = complexity.getCCNForProject(projectData);
        int  = projectData.getNumberOfValidBranches();
        double  = complexity.getCCNForProject(projectData);
        int  = projectData.getNumberOfCoveredLines();
        println((null + ) + null);
        println(null);
        int  = projectData.getNumberOfValidLines();
        int  = projectData.getNumberOfCoveredBranches();
    } finally {
        close();
    }
}
}