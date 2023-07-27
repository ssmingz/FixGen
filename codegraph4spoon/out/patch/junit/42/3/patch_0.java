class PlaceHold {
  public void removeListener(RunListener listener) {
    if (listener == null) {
      throw new NullPointerException("Cannot remove a null listener");
    }
    listeners.remove(wrapIfNotThreadSafe(listener));
  }
}
