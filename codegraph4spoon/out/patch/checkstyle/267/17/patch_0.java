class MissingSwitchDefaultCheck {
  public MissingSwitchDefaultCheck() {
    setLimitedTokens(Utils.getTokenName(LITERAL_DEFAULT));
    setMinimumNumber(1);
    setMaximumDepth(2);
    setMinimumMessage(MSG_KEY);
  }
}
