class PlaceHold {
  public long getFalseHits() {
    lock.lock();
    try {
      return this.falseHits;
    } finally {
      lock.unlock();
    }
  }
}
