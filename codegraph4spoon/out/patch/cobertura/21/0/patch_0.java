class PlaceHold {
  private void dumpPackage(PackageData packageData) {
    logger.debug("Dumping package " + packageData.getName());
    println(
        (((((("<package name=\"" + packageData.getName()) + "\" line-rate=\"")
                            + packageData.getLineCoverageRate())
                        + "\" branch-rate=\"")
                    + packageData.getBranchCoverageRate())
                + null)
            + ">");
    increaseIndentation();
    dumpClasses(packageData);
    decreaseIndentation();
    println("</package>");
  }
}
