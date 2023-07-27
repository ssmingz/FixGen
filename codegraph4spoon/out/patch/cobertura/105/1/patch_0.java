class PlaceHold {
  public long getTrueHits() {
    lock.lock();
    try {
      return this.trueHits;
    } finally {
      lock.unlock();
    }
  }
}
