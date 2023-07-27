class HistoryMethodAdapter {
  public HistoryMethodAdapter(MethodVisitor mv, int eventsToTrace) {
    super(mv, ASM4);
    this.eventsToTrace = eventsToTrace;
  }
}
