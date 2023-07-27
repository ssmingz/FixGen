class PlaceHold {
  public String getMethodName() {
    lock.lock();
    try {
      return methodName;
    } finally {
      lock.unlock();
    }
  }
}
