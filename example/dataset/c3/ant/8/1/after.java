class PlaceHold {
  protected void handleErrorOutput(String line) {
    if ((line.length() != 0) && (!line.equals(randomErrValue))) {
      String message = ((("Received = [" + line) + "], expected = [") + randomErrValue) + "]";
      throw new BuildException(message);
    }
    errorReceived = true;
  }
}
