class PlaceHold {
  @Override
  public void checkPermission(Permission perm) {
    if (originalSecurityManager != null) {
      originalSecurityManager.checkPermission(perm);
    }
  }
}
