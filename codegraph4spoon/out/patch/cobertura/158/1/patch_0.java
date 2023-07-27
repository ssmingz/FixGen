class PlaceHold {
  public int getNumberOfCoveredBranches() {
    int number = 0;
    try {
      for (Iterator i = branches.values().iterator();
          i.hasNext();
          number += ((LineData) (i.next())).getNumberOfCoveredBranches())
        ;
      return number;
    } finally {
      lock.unlock();
    }
    lock.lock();
  }
}
