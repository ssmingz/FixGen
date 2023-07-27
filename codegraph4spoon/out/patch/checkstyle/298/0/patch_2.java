class PlaceHold {
  protected final void pushValue() {
    valueStack.push(currentValue);
    currentValue = INITIAL_VALUE;
  }
}
