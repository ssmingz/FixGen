class PlaceHold {
  @Override
  public boolean isCellEditable(EventObject event) {
    if (event instanceof MouseEvent) {
      for (int counter = getColumnCount() - 1; counter >= 0; counter--) {
        if (getColumnClass(counter) == ParseTreeTableModel.class) {
          final MouseEvent mouseEvent = ((MouseEvent) (event));
          final MouseEvent newMouseEvent =
              new MouseEvent(
                  tree,
                  mouseEvent.getID(),
                  mouseEvent.getWhen(),
                  mouseEvent.getModifiers(),
                  mouseEvent.getX() - getCellRect(0, counter, true).x,
                  mouseEvent.getY(),
                  mouseEvent.getClickCount(),
                  mouseEvent.isPopupTrigger());
          tree.dispatchEvent(newMouseEvent);
          break;
        }
      }
    }
    return false;
  }
}
