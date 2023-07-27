class PlaceHold {
  public void fooMethod() {
    int a = 1;
    if (a == 1) {}
    char[] s = new char[] {'1', '2'};
    int index = 2;
    if (doSideEffect() == 1) {}
    while ((a = index - 1) != 0) {}
    for (; (index < s.length) && (s[index] != 'x'); index++) {}
    if (a == 1) {
    } else {
      System.identityHashCode(null);
    }
    switch (a) {
    }
    switch (a) {
      case 1:
        a = 2;
      case 2:
        a = 3;
      default:
        a = 0;
    }
  }
}
