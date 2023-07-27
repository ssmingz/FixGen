class MissingCtorCheck {
  public MissingCtorCheck() {
    setLimitedTokens(new String[] {TokenTypes.getTokenName(CTOR_DEF)});
    setMinimumNumber(1);
    setMaximumDepth(2);
    setMinimumMessage(MSG_KEY);
  }
}
