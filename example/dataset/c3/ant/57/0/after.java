class PlaceHold {
  protected final void configure(final Object object, final Configuration element)
      throws ConfigurationException {
    m_configurer.configure(object, element, getContext());
  }
}
