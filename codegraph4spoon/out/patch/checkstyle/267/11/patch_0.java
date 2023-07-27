class MissingCtorCheck {
  public MissingCtorCheck() {
    setLimitedTokens(Utils.getTokenName(CTOR_DEF));
    setMinimumNumber(1);
    setMaximumDepth(2);
    setMinimumMessage(MSG_KEY);
  }
}
