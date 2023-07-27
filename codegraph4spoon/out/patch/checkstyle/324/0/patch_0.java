class PlaceHold {
  public void setIllegalClassNames(String[] classNames) {
    illegalClassNames.clear();
    for (String name : classNames) {
      illegalClassNames.add(name);
    }
  }
}
