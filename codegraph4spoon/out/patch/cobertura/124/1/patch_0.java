class PlaceHold {
  public int getNumberOfCoveredBranches() {
    int number = 0;
    lock.lock();
    try {
      for (Iterator<LineData> i = branches.values().iterator();
          i.hasNext();
          number += ((LineData) (i.next())).getNumberOfCoveredBranches())
        ;
      return number;
    } finally {
      lock.unlock();
    }
  }
}
