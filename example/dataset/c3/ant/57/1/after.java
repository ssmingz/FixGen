class PlaceHold {
  protected final void configure(final Object object, final String name, final String value)
      throws ConfigurationException {
    m_configurer.configure(object, name, value, getContext());
  }
}
