class PlaceHold{
public void testScopeInnerInterfacesPublic() throws Exception {
    mConfig.setBooleanFlag(, null);
    mConfig.setIgnorePublicInInterface(true);
    final Checker c = createChecker();
    final String filepath = getPath("InputScopeInnerInterfaces.java");
    assertNotNull(c);
    final String[] expected = new String[]{ filepath + ":7: type is missing a Javadoc comment.", filepath + ":38: type is missing a Javadoc comment.", filepath + ":40:23: variable 'CA' missing Javadoc.", filepath + ":41:16: variable 'CB' missing Javadoc.", filepath + ":43:9: method is missing a Javadoc comment.", filepath + ":44:9: method is missing a Javadoc comment." };
    verify(c, filepath, expected);
}
}