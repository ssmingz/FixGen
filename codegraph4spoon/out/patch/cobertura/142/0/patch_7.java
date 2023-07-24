public XMLReport(ProjectData projectData, File destinationDir, FileFinder finder, ComplexityCalculator complexity) throws IOException {
    this.complexity = complexity;
    this.finder = finder;
    File file = new File(destinationDir, "coverage.xml");
    try {
        printHeader();
        int numBranchesCovered = projectData.getNumberOfCoveredBranches();
        int numBranchesValid = projectData.getNumberOfValidBranches();
        printCoverageElement(projectData, complexity);
        increaseIndentation();
        dumpSources();
        dumpPackages(projectData);
        decreaseIndentation();
        println("</coverage>");
        setPrintWriter(IOUtil.getPrintWriter(file));
    } finally {
        pw.close();
    }
}