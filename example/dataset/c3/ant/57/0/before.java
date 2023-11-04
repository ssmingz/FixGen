class PlaceHold {
  protected final void configure(final Object object, final Configuration element)
      throws ConfigurationException {
    getConfigurer().configure(object, element, getContext());
  }
}
