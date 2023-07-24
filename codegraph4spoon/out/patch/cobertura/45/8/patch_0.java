public DetectIgnoredCodeClassVisitor(ClassVisitor cv, boolean ignoreTrivial, Set<String> ignoreAnnotations) {
    super(cv, ASM4);
    this.ignoreTrivial = ignoreTrivial;
    this.ignoreAnnotations = ignoreAnnotations;
}