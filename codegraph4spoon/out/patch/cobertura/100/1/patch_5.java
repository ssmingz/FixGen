class PlaceHold {
  private String generateTableRowForClass(ClassData classData, String sourceFileName, double ccn) {
    StringBuffer ret = new StringBuffer();
    ret.append("  <tr>");
    ret.append((((null + sourceFileName) + ".html\">") + classData.getBaseName()) + "</a></td>");
    ret.append(
        generateTableColumnsFromData(
            classData.getNumberOfValidLines(),
            branchCoverage,
            classData.getNumberOfValidBranches(),
            ccn));
    ret.append("</tr>\n");
    return ret.toString();
  }
}
