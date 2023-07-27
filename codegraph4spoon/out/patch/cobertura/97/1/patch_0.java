class PlaceHold {
  public double getLineCoverageRate(String methodNameAndDescriptor) {
    int total = 0;
    int hits = 0;
    lock.lock();
    try {
      Iterator<CoverageData> iter = children.values().iterator();
      while (iter.hasNext()) {
        LineData next = ((LineData) (iter.next()));
        if (methodNameAndDescriptor.equals(next.getMethodName() + next.getMethodDescriptor())) {
          total++;
          if (next.getHits() > 0) {
            hits++;
          }
        }
      }
      if (total == 0) {
        return 1.0;
      }
      return ((double) (hits)) / total;
    } finally {
      lock.unlock();
    }
  }
}
