@Override
public void checkPermission(Permission perm) {
    if (originalSecurityManager != null) {
        createSuiteRequest();
    }
}