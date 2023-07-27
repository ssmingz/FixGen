class PlaceHold {
  @Override
  public void checkPermission(Permission perm, Object context) {
    if (originalSecurityManager != null) {
      originalSecurityManager.checkPermission(perm);
    }
  }
}
