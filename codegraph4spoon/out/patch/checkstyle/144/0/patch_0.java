class PlaceHold{
public void testImport() throws Exception {
    mConfig.setBooleanFlag(, null);
    final Checker c = createChecker();
    final String filepath = getPath("InputImport.java");
    assertNotNull(c);
    final String[] expected = new String[]{ filepath + ":7: Avoid using the '.*' form of import.", filepath + ":7: Redundant import from the same package.", filepath + ":8: Redundant import from the same package.", filepath + ":9: Avoid using the '.*' form of import.", filepath + ":10: Avoid using the '.*' form of import.", filepath + ":10: Redundant import from the java.lang package.", filepath + ":11: Redundant import from the java.lang package.", filepath + ":13: Unused import - java.util.List", filepath + ":14: Duplicate import to line 13.", filepath + ":14: Unused import - java.util.List", filepath + ":15: Import from illegal package - sun.net.ftpclient.FtpClient" };
    verify(c, filepath, expected);
}
}