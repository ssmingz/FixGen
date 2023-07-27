class PlaceHold {
  public int getNumberOfClasses() {
    lock.lock();
    try {
      return this.classes.size();
    } finally {
      lock.unlock();
    }
  }
}
