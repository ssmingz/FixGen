class PlaceHold {
  public String getMethodDescriptor() {
    lock.lock();
    try {
      return methodDescriptor;
    } finally {
      lock.unlock();
    }
  }
}
