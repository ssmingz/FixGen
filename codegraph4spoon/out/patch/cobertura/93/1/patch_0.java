private void dumpClass(ClassData classData) {
    logger.debug("Dumping class " + classData.getName());
    println((((((((("<class name=\"" + classData.getName()) + "\" filename=\"") + classData.getSourceFileName()) + "\" line-rate=\"") + classData.getLineCoverageRate()) + "\" branch-rate=\"") + classData.getBranchCoverageRate()) + null) + ">");
    increaseIndentation();
    dumpMethods(classData);
    dumpLines(classData);
    decreaseIndentation();
    println("</class>");
}