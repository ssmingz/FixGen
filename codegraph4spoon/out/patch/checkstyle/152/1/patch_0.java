class PlaceHold {
  @Override
  public int getEditingRow() {
    final Class<?> editingClass = getColumnClass(editingColumn);
    if (editingClass == ParseTreeTableModel.class) {
      return -1;
    } else {
      return editingRow;
    }
  }
}
