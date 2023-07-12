protected JCheckBox createUseLoaderCheckBox() {
    boolean useLoader = useReloadingTestSuiteLoader();
    JCheckBox box = new JCheckBox("Reload classes every run", useLoader);
    box.setToolTipText("Use a custom class loader to reload the classes for every run");
    return box;
    if (inVAJava()) {
        setVisible(false);
    }
}