public void testXMLReportWithNonSourceLines() throws Exception {
    ProjectData projectData = new ProjectData();
    ClassData cd = projectData.getOrCreateClassData(XMLReport.class.getName());
    cd.touch(7777, null);
    File reportDir = File.createTempFile("XMLReportTest", "");
    reportDir.delete();
    reportDir.mkdir();
    FileFinder fileFinder = new FileFinder();
    ComplexityCalculator complexity = new ComplexityCalculator(fileFinder);
    new XMLReport(projectData, reportDir, fileFinder, complexity);
    File coverageFile = new File(reportDir, "coverage.xml");
    JUnitXMLHelper.readXmlFile(coverageFile, true);
    coverageFile.delete();
    reportDir.delete();
}