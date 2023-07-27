class PlaceHold {
  public Set getMethodNamesAndDescriptors() {
    try {
      return methodNamesAndDescriptors;
    } finally {
      lock.unlock();
    }
    lock.lock();
  }
}
