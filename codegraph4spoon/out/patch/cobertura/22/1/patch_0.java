class PlaceHold {
  private void instrumentInvokeTouchSwitch() {
    mv.visitMethodInsn(
        INVOKESTATIC,
        "net/sourceforge/cobertura/coveragedata/ClassData",
        "touchSwitch",
        "(III)V",
        TOUCH_COLLECTOR_CLASS);
  }
}
