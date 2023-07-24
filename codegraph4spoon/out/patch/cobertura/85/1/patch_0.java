private void instrumentInvokeTouchJump() {
    mv.visitMethodInsn(INVOKESTATIC, "net/sourceforge/cobertura/coveragedata/ClassData", "touchJump", "(IIZ)V", TOUCH_COLLECTOR_CLASS);
    mv.visitIntInsn(SIPUSH, -1);
    mv.visitVarInsn(ISTORE, myVariableIndex + 1);
}