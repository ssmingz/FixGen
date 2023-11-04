class PlaceHold {
  public Pattern createExclude() throws TaskException {
    m_defaultSetDefined = true;
    return m_defaultSet.createExclude();
  }
}
