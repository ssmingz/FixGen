class PlaceHold {
  protected Frame createUI(String suiteName) {
    Frame frame = new Frame("JUnit");
    Image icon = loadFrameIcon();
    if (icon != null) {
      frame.setIconImage(icon);
    }
    frame.setLayout(new BorderLayout(0, 0));
    frame.setBackground(control);
    final Frame finalFrame = frame;
    frame.addWindowListener(
        new WindowAdapter() {
          public void windowClosing(WindowEvent e) {
            finalFrame.dispose();
            System.exit(0);
          }
        });
    MenuBar mb = new MenuBar();
    createMenus(mb);
    frame.setMenuBar(mb);
    Label suiteLabel = new Label("Test class name:");
    fSuiteField = new TextField(suiteName != null ? suiteName : "");
    fSuiteField.selectAll();
    fSuiteField.requestFocus();
    fSuiteField.setFont(PLAIN_FONT);
    fSuiteField.setColumns(40);
    fSuiteField.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            runSuite();
          }
        });
    fSuiteField.addTextListener(
        new TextListener() {
          public void textValueChanged(TextEvent e) {
            fRun.setEnabled(fSuiteField.getText().length() > 0);
            fStatusLine.setText("");
          }
        });
    fRun = new Button("Run");
    fRun.setEnabled(false);
    fRun.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            runSuite();
          }
        });
    boolean useLoader = useReloadingTestSuiteLoader();
    fUseLoadingRunner = new Checkbox("Reload classes every run", useLoader);
    if (inVAJava()) {
      fUseLoadingRunner.setVisible(false);
    }
    fProgressIndicator = new ProgressBar();
    fNumberOfErrors = new Label("0000", Label.RIGHT);
    fNumberOfErrors.setText("0");
    fNumberOfErrors.setFont(PLAIN_FONT);
    fNumberOfFailures = new Label("0000", Label.RIGHT);
    fNumberOfFailures.setText("0");
    fNumberOfFailures.setFont(PLAIN_FONT);
    fNumberOfRuns = new Label("0000", Label.RIGHT);
    fNumberOfRuns.setText("0");
    fNumberOfRuns.setFont(PLAIN_FONT);
    Panel numbersPanel = new Panel(new FlowLayout());
    numbersPanel.add(new Label("Runs:"));
    numbersPanel.add(fNumberOfRuns);
    numbersPanel.add(new Label("   Errors:"));
    numbersPanel.add(fNumberOfErrors);
    numbersPanel.add(new Label("   Failures:"));
    numbersPanel.add(fNumberOfFailures);
    Label failureLabel = new Label("Errors and Failures:");
    fFailureList = new List(5);
    fFailureList.addItemListener(
        new ItemListener() {
          public void itemStateChanged(ItemEvent e) {
            failureSelected();
          }
        });
    fRerunButton = new Button("Run");
    fRerunButton.setEnabled(false);
    fRerunButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            rerun();
          }
        });
    Panel failedPanel = new Panel(new GridLayout(0, 1, 0, 2));
    failedPanel.add(fRerunButton);
    fTraceArea = new TextArea();
    fTraceArea.setRows(5);
    fTraceArea.setColumns(60);
    fStatusLine = new TextField();
    fStatusLine.setFont(PLAIN_FONT);
    fStatusLine.setEditable(false);
    fStatusLine.setForeground(red);
    fQuitButton = new Button("Exit");
    fQuitButton.addActionListener(
        new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            System.exit(0);
          }
        });
    fLogo = new Logo();
    Panel panel = new Panel(new GridBagLayout());
    addGrid(panel, suiteLabel, 0, 0, 2, NONE, 1.0, WEST);
    addGrid(panel, fSuiteField, 0, 1, 2, HORIZONTAL, 1.0, WEST);
    addGrid(panel, fRun, 2, 1, 1, HORIZONTAL, 0.0, CENTER);
    addGrid(panel, fUseLoadingRunner, 0, 2, 2, HORIZONTAL, 1.0, WEST);
    addGrid(panel, fProgressIndicator, 0, 3, 2, HORIZONTAL, 1.0, WEST);
    addGrid(panel, fLogo, 2, 3, 1, NONE, 0.0, NORTH);
    addGrid(panel, numbersPanel, 0, 4, 2, NONE, 0.0, CENTER);
    addGrid(panel, failureLabel, 0, 5, 2, HORIZONTAL, 1.0, WEST);
    addGrid(panel, fFailureList, 0, 6, 2, BOTH, 1.0, WEST);
    addGrid(panel, failedPanel, 2, 6, 1, HORIZONTAL, 0.0, CENTER);
    addGrid(panel, fTraceArea, 0, 7, 2, BOTH, 1.0, WEST);
    addGrid(panel, fStatusLine, 0, 8, 2, HORIZONTAL, 1.0, CENTER);
    addGrid(panel, fQuitButton, 2, 8, 1, HORIZONTAL, 0.0, CENTER);
    frame.add(panel, BorderLayout.CENTER);
    frame.pack();
    return frame;
  }
}
