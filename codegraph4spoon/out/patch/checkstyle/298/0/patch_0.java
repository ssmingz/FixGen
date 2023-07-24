protected final void pushValue() {
    valueStack.push(mCurrentValue);
    mCurrentValue = INITIAL_VALUE;
}