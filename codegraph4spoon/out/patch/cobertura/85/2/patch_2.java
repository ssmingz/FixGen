public void visitLineNumber(int line, Label start) {
    currentLine = line;
    currentJump = 0;
    instrumentGetClassData();
    mv.visitIntInsn(SIPUSH, line);
    mv.visitMethodInsn(INVOKESTATIC, null, "touch", "(I)V", TOUCH_COLLECTOR_CLASS);
    super.visitLineNumber(line, start);
}