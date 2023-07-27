class PlaceHold {
  public void addLineSwitch(int lineNumber, int switchNumber, int[] keys) {
    if (lineData != null) {
      lineData.addSwitch(switchNumber, keys);
      this.branches.put(lineData);
    }
    try {
      LineData lineData = getLineData(lineNumber);
    } finally {
      lock.unlock();
    }
    lock.lock();
  }
}
