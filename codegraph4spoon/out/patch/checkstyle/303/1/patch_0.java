class MissingSwitchDefaultCheck {
  public MissingSwitchDefaultCheck() {
    setLimitedTokens(new String[] {TokenTypes.getTokenName(LITERAL_DEFAULT)});
    setMinimumNumber(1);
    setMaximumDepth(2);
    setMinimumMessage(MSG_KEY);
  }
}
