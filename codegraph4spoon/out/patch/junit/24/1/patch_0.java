public Statement apply(Statement base, Description description) {
    return new FailOnTimeout(base, fTimeout, fTimeUnit);
}