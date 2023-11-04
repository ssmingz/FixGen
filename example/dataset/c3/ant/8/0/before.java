class PlaceHold {
  protected void handleOutput(String line) {
    if (!line.equals(randomOutValue)) {
      String message = ((("Received = [" + line) + "], expected = [") + randomOutValue) + "]";
      throw new BuildException(message);
    }
    outputReceived = true;
  }
}
