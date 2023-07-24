public XMLReport(ProjectData projectData, File destinationDir, FileFinder finder, ComplexityCalculator complexity) throws IOException {
    this.complexity = complexity;
    this.finder = finder;
    File file = new File(destinationDir, "coverage.xml");
    setPrintWriter(IOUtil.getPrintWriter(file));
    try {
        println(null);
        printCoverageElement(projectData, complexity);
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
    } finally {
        close();
    }
}