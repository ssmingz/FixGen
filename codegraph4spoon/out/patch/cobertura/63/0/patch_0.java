public Collection getClasses() {
    try {
        return this.classes.values();
    } finally {
        lock.unlock();
    }
    lock.lock();
}