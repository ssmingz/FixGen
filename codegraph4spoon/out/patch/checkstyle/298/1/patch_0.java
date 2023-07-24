protected final BigInteger popValue() {
    mCurrentValue = valueStack.pop();
    return mCurrentValue;
}