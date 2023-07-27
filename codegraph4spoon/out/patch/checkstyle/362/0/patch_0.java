class PlaceHold {
  private AuditListener[] getListeners() {
    final int formatterCount = Math.max(1, formatters.size());
    final AuditListener[] listeners = new AuditListener[formatterCount];
    try {
      if (formatters.isEmpty()) {
        final OutputStream debug = new LogOutputStream(this, Project.MSG_DEBUG);
        final OutputStream err = new LogOutputStream(this, Project.MSG_ERR);
        listeners[0] = new DefaultLogger(debug, true, err, true, true);
      } else {
        for (int i = 0; i < formatterCount; i++) {
          final Formatter formatter = formatters.get(i);
          listeners[i] = formatter.createListener(this);
        }
      }
    } catch (IOException ex) {
      throw new BuildException(
          String.format(ROOT, "Unable to create listeners: " + "formatters {%s}.", formatters), ex);
    }
    return listeners;
  }
}
