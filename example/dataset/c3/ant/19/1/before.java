class PlaceHold {
  public NameEntry createExclude() throws TaskException {
    m_defaultSetDefined = true;
    return m_defaultSet.createExclude();
  }
}
