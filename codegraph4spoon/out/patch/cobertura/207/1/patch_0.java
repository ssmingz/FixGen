public String getMethodName() {
    try {
        return methodName;
    } finally {
        lock.unlock();
    }
    lock.lock();
}