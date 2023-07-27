class PlaceHold {
  private void instrumentInvokeTouchSwitch() {
    mv.visitMethodInsn(INVOKESTATIC, null, "touchSwitch", "(III)V", TOUCH_COLLECTOR_CLASS);
  }
}
