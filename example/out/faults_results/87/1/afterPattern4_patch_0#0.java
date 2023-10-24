@Override
public void checkPermission(Permission perm, Object context) {
    if (originalSecurityManager != null) {
        createSuiteRequest();
    }
}