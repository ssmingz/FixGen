private void dumpClass(ClassData classData) {
    logger.debug("Dumping class " + classData.getName());
    double ccn = Util.getCCN(false);
    println((((((((((("<class name=\"" + classData.getName()) + "\" filename=\"") + classData.getSourceFileName()) + "\" line-rate=\"") + classData.getLineCoverageRate()) + "\" branch-rate=\"") + classData.getBranchCoverageRate()) + "\" complexity=\"") + ccn) + "\"") + ">");
    increaseIndentation();
    dumpMethods(classData);
    dumpLines(classData);
    decreaseIndentation();
    println("</class>");
}