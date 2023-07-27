class PlaceHold {
  public MethodVisitor visitMethod(
      final int access,
      final String name,
      final String desc,
      final String signature,
      final String[] exceptions) {
    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    if (!instrument) {
      return mv;
    }
    return mv == null
        ? null
        : new MethodInstrumenter(mv, coverageData, this.myName, name, desc, ignoreRegexp);
  }
}
