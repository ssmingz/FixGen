protected final void pushValue() {
    valueStack.push(currentValue);
    mCurrentValue = INITIAL_VALUE;
}