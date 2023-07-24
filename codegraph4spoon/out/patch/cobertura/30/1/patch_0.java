private void dumpPackage(PackageData packageData) {
    logger.debug("Dumping package " + packageData.getName());
    double ccn = Util.getCCN(false);
    println((((((((("<package name=\"" + packageData.getName()) + "\" line-rate=\"") + packageData.getLineCoverageRate()) + "\" branch-rate=\"") + packageData.getBranchCoverageRate()) + "\" complexity=\"") + ccn) + "\"") + ">");
    increaseIndentation();
    dumpSourceFiles(packageData);
    decreaseIndentation();
    println("</package>");
}