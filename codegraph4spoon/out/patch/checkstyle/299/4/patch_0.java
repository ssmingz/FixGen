public void testSetterGetterOff() throws Exception {
    final String[] expected = new String[]{ "5:5: Missing a Javadoc comment.", "10:5: Missing a Javadoc comment.", "15:5: Missing a Javadoc comment.", "20:5: Missing a Javadoc comment.", "26:5: Missing a Javadoc comment.", "30:5: Missing a Javadoc comment.", "35:5: Missing a Javadoc comment.", "41:5: Missing a Javadoc comment.", "46:5: Missing a Javadoc comment." };
    verify(checkConfig, getPath(("javadoc" + File.separator) + "InputSetterGetter.java"), expected);
}