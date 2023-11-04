class PlaceHold {
  protected final void configure(final Object object, final String name, final String value)
      throws ConfigurationException {
    getConfigurer().configure(object, name, value, getContext());
  }
}
