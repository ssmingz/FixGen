public String getMethodDescriptor() {
    try {
        return methodDescriptor;
    } finally {
        lock.unlock();
    }
    lock.lock();
}