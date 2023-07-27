class PlaceHold {
  private String generateTableRowForPackage(PackageData packageData) {
    StringBuffer ret = new StringBuffer();
    String url1 = ("frame-summary-" + packageData.getName()) + ".html";
    String url2 = ("frame-sourcefiles-" + packageData.getName()) + ".html";
    double branchCoverage = -1;
    double ccn = complexity.getCCNForPackage(packageData);
    if (packageData.getNumberOfValidLines() > 0) {
      lineCoverage = packageData.getLineCoverageRate();
    }
    if (packageData.getNumberOfValidBranches() > 0) {
      branchCoverage = packageData.getBranchCoverageRate();
    }
    ret.append("  <tr>");
    ret.append(
        (((((null + url1) + "\" onclick=\'parent.sourceFileList.location.href=\"") + url2)
                    + "\"\'>")
                + generatePackageName(packageData))
            + "</a></td>");
    ret.append(("<td class=\"value\">" + packageData.getNumberOfChildren()) + "</td>");
    ret.append(
        generateTableColumnsFromData(
            lineCoverage,
            packageData.getNumberOfValidLines(),
            branchCoverage,
            packageData.getNumberOfValidBranches(),
            ccn));
    ret.append("</tr>");
    return ret.toString();
  }
}
