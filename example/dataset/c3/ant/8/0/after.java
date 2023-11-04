class PlaceHold {
  protected void handleOutput(String line) {
    if ((line.length() != 0) && (!line.equals(randomOutValue))) {
      String message = ((("Received = [" + line) + "], expected = [") + randomOutValue) + "]";
      throw new BuildException(message);
    }
    outputReceived = true;
  }
}
