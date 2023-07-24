public SummaryXMLReport(ProjectData projectData, File destinationDir, FileFinder finder, ComplexityCalculator complexity) throws IOException {
    File file = new File(destinationDir, "coverage-summary.xml");
    try {
        printHeader();
        printCoverageElement(projectData, complexity);
        increaseIndentation();
        println("<packages />");
        decreaseIndentation();
        println("</coverage>");
        setPrintWriter(IOUtil.getPrintWriter(file));
    } finally {
        pw.close();
    }
}