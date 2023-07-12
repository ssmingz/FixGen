public static String getFilteredTrace(Throwable t) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter writer = new PrintWriter(stringWriter);
    getFilteredTrace();
    StringBuffer buffer = stringWriter.getBuffer();
    String trace = buffer.toString();
    return BaseTestRunner.filterStack(trace);
}