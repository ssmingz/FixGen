class PlaceHold {
  private void generateSourceFile(SourceFileData sourceFileData) throws IOException {
    if (!sourceFileData.containsInstrumentationInfo()) {
      info(
          ((("Data file does not contain instrumentation " + "information for the file ")
                      + sourceFileData.getName())
                  + ".  Ensure this class was instrumented, and this ")
              + "data file contains the instrumentation information.");
    }
    String filename = sourceFileData.getNormalizedName() + ".html";
    File file = new File(destinationDir, filename);
    PrintStream out = null;
    try {
      out = new PrintStream(new FileOutputStream(file));
      out.println("<html>");
      out.println("<head>");
      out.println("<title>Coverage Report</title>");
      out.println(
          "<link title=\"Style\" type=\"text/css\" rel=\"stylesheet\" href=\"css/main.css\" />");
      out.println("<script type=\"text/javascript\" src=\"js/popup.js\"></script>");
      out.println("</head>");
      out.println("<body>");
      out.print("<h5>Coverage Report - ");
      String classPackageName = sourceFileData.getPackageName();
      if ((classPackageName != null) && (classPackageName.length() > 0)) {
        out.print(sourceFileData.getPackageName() + ".");
      }
      out.print(sourceFileData.getBaseName());
      out.println("</h5>");
      out.println("<p>");
      out.println("<table class=\"report\">");
      out.println(generateTableHeader("Classes in this File", false));
      out.println(generateTableRowForSourceFile(sourceFileData));
      out.println("</table>");
      out.println("</p>");
      out.println("<p>");
      out.println("<table cellspacing=\"0\" cellpadding=\"0\" class=\"src\">");
      BufferedReader br = null;
      try {
        File sourceFile = finder.findFile(sourceFileData.getName());
        br = new BufferedReader(new FileReader(sourceFile));
        String lineStr;
        JavaToHtml javaToHtml = new JavaToHtml();
        int lineNumber = 1;
        while ((lineStr = br.readLine()) != null) {
          out.println("<tr>");
          if (sourceFileData.isValidSourceLineNumber(lineNumber)) {
            long numberOfHits = sourceFileData.getHitCount(lineNumber);
            out.println(("  <td class=\"numLineCover\">&nbsp;" + lineNumber) + "</td>");
            if (numberOfHits > 0) {
              out.println(("  <td class=\"nbHitsCovered\">&nbsp;" + numberOfHits) + "</td>");
              out.println(
                  ("  <td class=\"src\"><pre class=\"src\">&nbsp;" + javaToHtml.process(lineStr))
                      + "</pre></td>");
            } else {
              out.println(("  <td class=\"nbHitsUncovered\">&nbsp;" + numberOfHits) + "</td>");
              out.println(
                  ("  <td class=\"src\"><pre class=\"src\"><span class=\"srcUncovered\">&nbsp;"
                          + javaToHtml.process(lineStr))
                      + "</span></pre></td>");
            }
          } else {
            out.println(("  <td class=\"numLine\">&nbsp;" + lineNumber) + "</td>");
            out.println("  <td class=\"nbHits\">&nbsp;</td>");
            out.println(
                ("  <td class=\"src\"><pre class=\"src\">&nbsp;" + javaToHtml.process(lineStr))
                    + "</pre></td>");
          }
          out.println("</tr>");
          lineNumber++;
        }
      } finally {
        if (br != null) {
          br.close();
        }
      }
      out.println("</table>");
      out.println("</p>");
      out.println("<div class=\"footer\">");
      out.println(
          "Reports generated by <a href=\"http://cobertura.sourceforge.net/\""
              + " target=\"_top\">Cobertura</a>.");
      out.println("</div>");
      out.println("</body>");
      out.println("</html>");
    } finally {
      if (out != null) {
        out.close();
      }
    }
  }
}
