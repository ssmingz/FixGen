class PlaceHold {
  public void addLine(int lineNumber, String methodName, String methodDescriptor) {
    LineData lineData = getLineData(lineNumber);
    if (lineData == null) {
      lineData = new LineData(lineNumber);
      children.put(new Integer(lineNumber), lineData);
    }
    lineData.setMethodNameAndDescriptor(methodName, methodDescriptor);
    methodNamesAndDescriptors.add(methodName + methodDescriptor);
  }
}
