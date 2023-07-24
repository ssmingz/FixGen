public ContextMethodAwareMethodAdapter(MethodVisitor mv, String className, String methodName, String methodSignature, AtomicInteger lineIdGenerator) {
    super(mv, ASM4);
    this.className = className;
    this.methodName = methodName;
    this.methodSignature = methodSignature;
    lastLineId = 0;
    this.lineIdGenerator = lineIdGenerator;
}