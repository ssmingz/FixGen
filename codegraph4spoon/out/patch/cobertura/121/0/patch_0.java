class PlaceHold {
  private static void touchIteratively(SwitchData data, int num) {
    for (int i = 0; i < 2000; i++) {
      Thread.yield();
      data.touchBranch(i, null);
    }
  }
}
